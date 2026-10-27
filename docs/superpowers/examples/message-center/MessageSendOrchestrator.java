package io.github.genkidoudou.message.internal.service;

import io.github.genkidoudou.common.message.ChannelType;
import io.github.genkidoudou.common.message.MessageChannel;
import io.github.genkidoudou.common.message.MessageChannelRequest;
import io.github.genkidoudou.common.message.MessageChannelResult;
import io.github.genkidoudou.common.message.MessageSendCommand;
import io.github.genkidoudou.common.message.MessageSendCommand.Receivers;
import io.github.genkidoudou.common.message.MessageSendResult;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 发送编排示例。
 * <p>
 * 不参与编译。演示设计文档第 6 节和第 9 节的分支，持久化用文件底部的协作接口表示。
 * 落地时这些接口换成发送单 Service、通道 Service 和 system.api。
 * <p>
 * 投递在调用线程内做完再返回。某一个通道失败只写它自己的明细。
 */
public class MessageSendOrchestrator {

    private final SendStore sendStore;
    private final ChannelStore channelStore;
    private final Map<ChannelType, MessageChannel> channelsByType;
    private final InboxAudienceResolver inboxAudienceResolver;

    public MessageSendOrchestrator(SendStore sendStore,
                                   ChannelStore channelStore,
                                   List<MessageChannel> channels,
                                   InboxAudienceResolver inboxAudienceResolver) {
        this.sendStore = sendStore;
        this.channelStore = channelStore;
        this.inboxAudienceResolver = inboxAudienceResolver;
        this.channelsByType = new java.util.EnumMap<>(ChannelType.class);
        for (MessageChannel channel : channels) {
            // 同一类型只保留一个实现。多条钉钉群是多条通道配置，不是多个实现类。
            this.channelsByType.put(channel.channelType(), channel);
        }
    }

    /**
     * @param command 标题、正文、通道、可选接收人和业务地址
     * @param source  API 或 ADMIN。业务入口传 API，管理端传 ADMIN
     * @return 终态发送单。没有异常也要检查总状态是不是 PARTIAL 或 FAIL
     */
    public MessageSendResult send(MessageSendCommand command, String source) {
        validate(command);
        // 空接收人与未传等价，后面的站内信判断只看 null。
        Receivers receivers = normalize(command.receivers());
        Long sendId = sendStore.insertPending(command, source, receivers);

        List<String> channelIds = distinct(command.channelIds());
        List<MessageSendResult.Detail> details = new ArrayList<>();
        for (String channelId : channelIds) {
            // 单条失败留在明细里，循环继续。不要把异常抛回业务调用方。
            details.add(dispatchOne(sendId, channelId, command, receivers));
        }
        String status = summarize(details);
        sendStore.finish(sendId, status);
        return new MessageSendResult(sendId, status, details);
    }

    /**
     * 处理一条通道。返回值用请求中的通道 ID，方便调用方和入参对照。
     */
    private MessageSendResult.Detail dispatchOne(Long sendId, String channelId,
                                                 MessageSendCommand command, Receivers receivers) {
        ChannelRow channel = channelStore.findEnabledOrAny(channelId);
        if (channel == null) {
            sendStore.insertDetail(sendId, null, null, "FAIL", "通道不存在", null);
            return new MessageSendResult.Detail(channelId, "FAIL", "通道不存在");
        }
        if (!"0".equals(channel.status())) {
            sendStore.insertDetail(sendId, channel, null, "SKIPPED", "通道已停用", null);
            return new MessageSendResult.Detail(channelId, "SKIPPED", "通道已停用");
        }

        String audience = null;
        Receivers inboxReceivers = null;
        if (channel.type() == ChannelType.INBOX) {
            // 传入接收人时，本次所有站内信通道都用这同一批人，不用通道上的默认用户。
            if (receivers != null) {
                audience = "REQUEST";
                inboxReceivers = receivers;
            } else {
                audience = "CHANNEL";
                inboxReceivers = channel.defaultReceivers();
            }
            List<Long> userIds = inboxAudienceResolver.resolve(inboxReceivers);
            if (userIds.isEmpty()) {
                sendStore.insertDetail(sendId, channel, audience, "SKIPPED", "未指定接收人", "0 人");
                return new MessageSendResult.Detail(channelId, "SKIPPED", "未指定接收人");
            }
            // 部门已经展开成用户 ID，通道实现只负责写入收件箱。
            inboxReceivers = new Receivers(userIds, List.of());
        }

        try {
            MessageChannel implementation = channelsByType.get(channel.type());
            MessageChannelResult result = implementation.send(new MessageChannelRequest(
                    sendId,
                    channel.id(),
                    channel.configJson(),
                    command.title(),
                    command.content(),
                    inboxReceivers,
                    audience,
                    command.bizUrl()));
            String detailStatus = result.success() ? "SUCCESS" : "FAIL";
            sendStore.insertDetail(sendId, channel, audience, detailStatus, result.errorMsg(), result.targetDigest());
            return new MessageSendResult.Detail(channelId, detailStatus, result.errorMsg());
        } catch (RuntimeException ex) {
            sendStore.insertDetail(sendId, channel, audience, "FAIL", ex.getMessage(), null);
            return new MessageSendResult.Detail(channelId, "FAIL", ex.getMessage());
        }
    }

    /**
     * 全部成功才是 SUCCESS。有成功也有失败或跳过是 PARTIAL。一条都没成功是 FAIL。
     * 停用通道造成的跳过也会让整单变成 PARTIAL。
     */
    private String summarize(List<MessageSendResult.Detail> details) {
        long success = details.stream().filter(item -> "SUCCESS".equals(item.status())).count();
        if (success == details.size()) {
            return "SUCCESS";
        }
        return success > 0 ? "PARTIAL" : "FAIL";
    }

    private void validate(MessageSendCommand command) {
        if (command.channelIds() == null || command.channelIds().isEmpty()) {
            throw new IllegalArgumentException("请选择通道");
        }
        if (command.title() == null || command.title().isBlank() || command.title().length() > 200) {
            throw new IllegalArgumentException("标题不能为空且不超过 200 字");
        }
        if (command.content() == null || command.content().isBlank()) {
            throw new IllegalArgumentException("正文不能为空");
        }
        String bizUrl = command.bizUrl();
        if (bizUrl != null && !bizUrl.isBlank() && !isBizUrl(bizUrl)) {
            // 非法地址在落单之前拒绝，避免收件箱里出现无法识别的链接。
            throw new IllegalArgumentException("业务地址只允许站内路径，或以 http://、https:// 开头");
        }
    }

    /**
     * 站内路径必须是一个斜杠开头，拒绝 //host 这种协议相对地址。
     * http 与 https 允许任意主机，这是当前设计的已知限制。
     */
    static boolean isBizUrl(String url) {
        if (url.startsWith("/") && !url.startsWith("//")) {
            return true;
        }
        return url.startsWith("https://") || url.startsWith("http://");
    }

    private Receivers normalize(Receivers receivers) {
        if (receivers == null || receivers.empty()) {
            return null;
        }
        return receivers;
    }

    private List<String> distinct(List<String> channelIds) {
        return new ArrayList<>(new LinkedHashSet<>(channelIds));
    }

    /** 通道行。status 使用 COMMON_STATUS，0 为正常。 */
    public record ChannelRow(String id, String name, ChannelType type, String status,
                             String configJson, Receivers defaultReceivers) {
    }

    /** 把部门展开成状态正常的用户，并与直接指定的用户去重。 */
    public interface InboxAudienceResolver {
        List<Long> resolve(Receivers receivers);
    }

    /** 示例用的持久化边界。落地时对应发送单与明细的 Mapper。 */
    public interface SendStore {
        Long insertPending(MessageSendCommand command, String source, Receivers receivers);

        void insertDetail(Long sendId, ChannelRow channel, String audience,
                          String status, String errorMsg, String targetDigest);

        void finish(Long sendId, String status);
    }

    /** 查不到或已逻辑删除时返回 null。停用的通道仍返回，由编排层记跳过。 */
    public interface ChannelStore {
        ChannelRow findEnabledOrAny(String channelId);
    }
}
