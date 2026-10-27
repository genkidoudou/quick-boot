package io.github.genkidoudou.system.internal.message;

import io.github.genkidoudou.system.internal.message.MessageSendService.ChannelRow;
import io.github.genkidoudou.system.internal.message.MessageSendService.SendCommand;
import io.github.genkidoudou.system.internal.message.MessageSendService.SendOutcome;
import io.github.genkidoudou.system.internal.message.MessageTypes.MessageSource;

import java.util.List;
import java.util.Map;

/**
 * 外部系统投递入口。示例，不参与编译。
 * <p>
 * 对应 {@code POST message/open/send}。不走登录会话，只认请求头 {@code X-Message-Token}。
 * token 属于哪个渠道，消息就只走哪个渠道，请求体里不能再选渠道。
 * 日志不要打印 token。
 */
public class OpenSendExample {

    public static final String TOKEN_HEADER = "X-Message-Token";

    private final ChannelLookup channels;
    private final MessageSendService sendService;

    public OpenSendExample(ChannelLookup channels, MessageSendService sendService) {
        this.channels = channels;
        this.sendService = sendService;
    }

    /**
     * @param token 请求头里的入站 token。空、对不上、渠道已停用都拒绝，并且不产生消息
     * @param body  标题、变量、用户、群组。用户和群组都可以空，空则按「没有收件人」产生失败消息
     */
    public OpenResponse send(String token, OpenBody body) {
        if (token == null || token.isBlank()) {
            return OpenResponse.rejected("token 无效");
        }
        ChannelRow channel = channels.findEnabledByToken(token).orElse(null);
        if (channel == null) {
            // 停用渠道和未知 token 是同一种拒绝，避免用响应差别探测 token。
            return OpenResponse.rejected("token 无效");
        }
        if (body.title() == null || body.title().isBlank()) {
            return OpenResponse.rejected("标题不能为空白");
        }
        Map<String, Object> variables = body.variables() == null ? Map.of() : body.variables();
        SendOutcome outcome = sendService.send(new SendCommand(
                MessageSource.API,
                body.title().trim(),
                variables,
                body.userIds() == null ? List.of() : body.userIds(),
                body.groupIds() == null ? List.of() : body.groupIds(),
                List.of(channel.id())));
        if (!outcome.accepted()) {
            return OpenResponse.rejected(outcome.rejectReason());
        }
        return OpenResponse.accepted(outcome.result().messageId(), outcome.result().status().name(), outcome.result().channels());
    }

    public record OpenBody(String title,
                           Map<String, Object> variables,
                           List<Long> userIds,
                           List<Long> groupIds) {
    }

    /**
     * 业务失败（没有收件人、群组不存在、部分渠道失败）带消息 id。
     * token 错误不带消息 id。
     */
    public record OpenResponse(boolean accepted,
                               String error,
                               Long messageId,
                               String status,
                               List<MessageSendService.ChannelSnapshot> channels) {

        static OpenResponse rejected(String error) {
            return new OpenResponse(false, error, null, null, List.of());
        }

        static OpenResponse accepted(long messageId,
                                     String status,
                                     List<MessageSendService.ChannelSnapshot> channels) {
            return new OpenResponse(true, null, messageId, status, channels);
        }
    }

    /** 只返回启用中的渠道。停用返回 empty，由入口当成 token 无效。 */
    public interface ChannelLookup {

        java.util.Optional<ChannelRow> findEnabledByToken(String token);
    }
}
