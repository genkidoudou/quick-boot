package io.github.genkidoudou.system.internal.message;

import io.github.genkidoudou.system.api.message.MessageSender;
import io.github.genkidoudou.system.api.message.MessageSender.MessageDelivery;
import io.github.genkidoudou.system.internal.message.MessageTypes.ChannelType;
import io.github.genkidoudou.system.internal.message.MessageTypes.DeliveryStatus;
import io.github.genkidoudou.system.internal.message.MessageTypes.MessageSource;
import io.github.genkidoudou.system.internal.message.MessageTypes.MessageStatus;
import io.github.genkidoudou.system.internal.message.RecipientResolver.DeptTarget;
import io.github.genkidoudou.system.internal.message.RecipientResolver.GroupAudience;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端和外部调用共用的发送流程。示例，不参与编译。
 * <p>
 * 对应设计文档第 5 节。投递在一次调用里做完，不自动重试。
 * 一个渠道失败只记在自己的快照上，不撤回已经成功的渠道。
 */
public class MessageSendService {

    private static final int ERROR_LIMIT = 500;
    private static final int WEBHOOK_TIMEOUT_SECONDS = 10;

    private final MessageStore store;
    private final RecipientResolver recipients;
    private final WebhookClient webhookClient;
    private final SenderLookup senderLookup;

    public MessageSendService(MessageStore store,
                              RecipientResolver recipients,
                              WebhookClient webhookClient,
                              SenderLookup senderLookup) {
        this.store = store;
        this.recipients = recipients;
        this.webhookClient = webhookClient;
        this.senderLookup = senderLookup;
    }

    /**
     * 发送一条消息。
     * <p>
     * 管理端没选渠道、选了两个站内渠道、外部 token 对不上或渠道已停用：返回拒绝，不写消息。
     * 群组不存在、没有收件人、渠道投递失败：都写消息，并返回消息 id。
     */
    public SendOutcome send(SendCommand command) {
        if (command.channelIds().isEmpty()) {
            return SendOutcome.rejected("未选渠道");
        }
        List<ChannelRow> selected = new ArrayList<>();
        int inAppCount = 0;
        for (Long channelId : command.channelIds()) {
            ChannelRow channel = store.findChannel(channelId).orElse(null);
            if (channel != null && channel.type() == ChannelType.IN_APP) {
                inAppCount++;
            }
            selected.add(channel);
        }
        if (inAppCount > 1) {
            return SendOutcome.rejected("站内渠道最多一个");
        }

        Long missingGroupId = firstMissingGroup(command.groupIds());
        if (missingGroupId != null) {
            // 群组 id 写错是整次失败，但请求里的用户和群组要留下来，方便对账。
            long messageId = store.insertFailedMessage(command, "群组不存在：" + missingGroupId);
            return SendOutcome.accepted(store.loadResult(messageId));
        }

        List<GroupAudience> groups = command.groupIds().stream().map(store::loadGroup).toList();
        List<Long> resolved = recipients.resolve(command.directUserIds(), groups);
        if (resolved.isEmpty()) {
            long messageId = store.insertFailedMessage(command, "没有收件人");
            return SendOutcome.accepted(store.loadResult(messageId));
        }

        long messageId = store.insertMessage(command, resolved);
        int success = 0;
        for (int i = 0; i < command.channelIds().size(); i++) {
            Long channelId = command.channelIds().get(i);
            ChannelRow channel = selected.get(i);
            if (deliverOne(messageId, command.title(), command.variables(), resolved, channelId, channel)) {
                success++;
            }
        }
        int total = command.channelIds().size();
        MessageStatus status = success == total
                ? MessageStatus.SUCCESS
                : success == 0 ? MessageStatus.FAILED : MessageStatus.PARTIAL;
        // 全部渠道失败时，原因写在各渠道快照上，消息本身的失败原因留空。
        store.finish(messageId, status, null);
        return SendOutcome.accepted(store.loadResult(messageId));
    }

    /**
     * @return 这个渠道是否投递成功
     */
    private boolean deliverOne(long messageId,
                               String title,
                               Map<String, Object> variables,
                               List<Long> resolved,
                               Long channelId,
                               ChannelRow channel) {
        if (channel == null) {
            // 渠道已经没了，仍然留一行快照，模板正文为空。
            store.insertChannelSnapshot(messageId, new ChannelSnapshot(
                    channelId, null, "", null, null, null, DeliveryStatus.FAILED, "渠道不存在"));
            return false;
        }
        if (!channel.enabled()) {
            store.insertChannelSnapshot(messageId, snapshotOf(channel, null, DeliveryStatus.FAILED, "渠道已停用"));
            return false;
        }
        TemplateRenderer.Rendered rendered = TemplateRenderer.render(channel.templateBody(), variables);
        if (!rendered.ok()) {
            store.insertChannelSnapshot(messageId, snapshotOf(channel, null, DeliveryStatus.FAILED, rendered.error()));
            return false;
        }
        try {
            dispatch(channel, title, rendered.content(), resolved, messageId);
        } catch (RuntimeException ex) {
            store.insertChannelSnapshot(messageId, snapshotOf(
                    channel, rendered.content(), DeliveryStatus.FAILED, clip(ex.getMessage())));
            return false;
        }
        store.insertChannelSnapshot(messageId, snapshotOf(
                channel, rendered.content(), DeliveryStatus.SUCCESS, null));
        return true;
    }

    /** 站内写收件箱；Webhook POST 一次；Bean 按快照里的名称调用。三者互不影响。 */
    private void dispatch(ChannelRow channel,
                          String title,
                          String content,
                          List<Long> resolved,
                          long messageId) {
        switch (channel.type()) {
            case IN_APP -> store.insertInbox(messageId, resolved);
            case WEBHOOK -> webhookClient.post(
                    channel.webhookUrl(),
                    payload(title, channel, content, resolved),
                    WEBHOOK_TIMEOUT_SECONDS);
            case BEAN -> senderLookup.require(channel.beanName()).send(
                    new MessageDelivery(title, channel.contentType().name(), content, resolved));
        }
    }

    /** Webhook 正文和 Bean 入参字段一致。不放 inbound token。 */
    private Map<String, Object> payload(String title, ChannelRow channel, String content, List<Long> resolved) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("contentType", channel.contentType().name());
        body.put("content", content);
        body.put("recipientUserIds", resolved);
        return body;
    }

    private ChannelSnapshot snapshotOf(ChannelRow channel,
                                       String rendered,
                                       DeliveryStatus status,
                                       String error) {
        return new ChannelSnapshot(
                channel.id(),
                channel.contentType().name(),
                channel.templateBody(),
                rendered,
                channel.type() == ChannelType.WEBHOOK ? channel.webhookUrl() : null,
                channel.type() == ChannelType.BEAN ? channel.beanName() : null,
                status,
                error);
    }

    private Long firstMissingGroup(List<Long> groupIds) {
        for (Long groupId : groupIds) {
            if (!store.groupExists(groupId)) {
                return groupId;
            }
        }
        return null;
    }

    /** 失败原因最长 500 字。空信息写成「投递失败」，避免快照上看不到原因。 */
    private static String clip(String message) {
        if (message == null || message.isBlank()) {
            return "投递失败";
        }
        return message.length() <= ERROR_LIMIT ? message : message.substring(0, ERROR_LIMIT);
    }

    /**
     * 一次发送请求。channelIds 对外部调用只有 token 所属的那一个。
     *
     * @param source        ADMIN 或 API
     * @param title         不能为空白，由入口先校验
     * @param variables     缺省 {@code {}}。null 值会在渲染时当成缺少该键
     * @param directUserIds 直接用户，可以空
     * @param groupIds      群组，可以空
     * @param channelIds    管理端多选；外部调用固定一个
     */
    public record SendCommand(MessageSource source,
                              String title,
                              Map<String, Object> variables,
                              List<Long> directUserIds,
                              List<Long> groupIds,
                              List<Long> channelIds) {
    }

    /** 拒绝时没有消息 id。接受时带消息状态和各渠道快照。 */
    public record SendOutcome(boolean accepted, String rejectReason, SendResult result) {

        static SendOutcome rejected(String reason) {
            return new SendOutcome(false, reason, null);
        }

        static SendOutcome accepted(SendResult result) {
            return new SendOutcome(true, null, result);
        }
    }

    public record SendResult(long messageId, MessageStatus status, String failReason, List<ChannelSnapshot> channels) {
    }

    public record ChannelSnapshot(Long channelId,
                                  String contentType,
                                  String templateBody,
                                  String renderedBody,
                                  String webhookUrl,
                                  String beanName,
                                  DeliveryStatus deliveryStatus,
                                  String errorMessage) {
    }

    /** 发送时读到的渠道。enabled 为 false 表示停用，仍然可以把模板抄进快照。 */
    public record ChannelRow(Long id,
                             ChannelType type,
                             MessageTypes.ContentType contentType,
                             String templateBody,
                             String webhookUrl,
                             String beanName,
                             boolean enabled) {
    }

    /** 持久化口。落地时换成 Mapper。没有收件人、群组不存在时不写收件人快照和渠道快照。 */
    public interface MessageStore {

        boolean groupExists(Long groupId);

        GroupAudience loadGroup(Long groupId);

        java.util.Optional<ChannelRow> findChannel(Long channelId);

        /** 整次未投递。保存标题、变量、直接用户、请求中的群组，不写渠道快照。 */
        long insertFailedMessage(SendCommand command, String failReason);

        /** 已经展开出收件人。先写下消息和收件人快照，渠道快照在投递时逐行补。 */
        long insertMessage(SendCommand command, List<Long> recipientUserIds);

        void insertChannelSnapshot(long messageId, ChannelSnapshot snapshot);

        /** 站内渠道渲染成功后，每个收件人一行，read_at 为空。 */
        void insertInbox(long messageId, List<Long> recipientUserIds);

        void finish(long messageId, MessageStatus status, String failReason);

        SendResult loadResult(long messageId);
    }

    /**
     * 向渠道上抄下来的地址 POST 一次。超时 10 秒。
     * HTTP 2xx 正常返回；超时、网络错误、其他状态码抛出运行时异常，由发送层记成该渠道失败。
     */
    public interface WebhookClient {

        void post(String url, Map<String, Object> jsonBody, int timeoutSeconds);
    }

    /**
     * 按 Bean 名称取出 {@link MessageSender}。
     * 名称不存在或类型不对时抛出异常，不在这里吞掉。
     */
    public interface SenderLookup {

        MessageSender require(String beanName);
    }
}
