package io.github.genkidoudou.message.internal.channel;

import io.github.genkidoudou.common.message.ChannelType;
import io.github.genkidoudou.common.message.MessageChannel;
import io.github.genkidoudou.common.message.MessageChannelRequest;
import io.github.genkidoudou.common.message.MessageChannelResult;
import io.github.genkidoudou.common.message.MessageSendCommand.Receivers;

/**
 * 自定义 Webhook 通道示例。
 * <p>
 * 不参与编译。地址永远来自通道配置。receivers 只作为模板数据，不决定请求打到哪里。
 * 模板只做四个占位符的原文替换，不计算表达式，避免正文里的 ${} 被当成脚本。
 */
public class WebhookMessageChannel implements MessageChannel {

    private static final String DEFAULT_TEMPLATE = """
            {"title":"${title}","content":"${content}","bizUrl":"${bizUrl}","receivers":"${receivers}"}
            """;

    @Override
    public ChannelType channelType() {
        return ChannelType.WEBHOOK;
    }

    @Override
    public MessageChannelResult send(MessageChannelRequest request) {
        // 按 title、content、bizUrl、receivers 的顺序替换。
        // 若标题里本身写了 ${content}，后面的替换还会动到它。落地时不要直接链式 replace。
        String body = DEFAULT_TEMPLATE
                .replace("${title}", nullToEmpty(request.title()))
                .replace("${content}", nullToEmpty(request.content()))
                .replace("${bizUrl}", nullToEmpty(request.bizUrl()))
                .replace("${receivers}", formatReceivers(request.receivers()));
        // method、headers、url 都从通道配置读取。落地时限制 method 为 GET 或 POST。
        int status = post(body);
        if (status >= 200 && status < 300) {
            return MessageChannelResult.success("Webhook 已请求");
        }
        return MessageChannelResult.fail("Webhook 已请求", "HTTP " + status);
    }

    /**
     * 模板里的接收人只用于展示和透传。这里输出用户 ID、部门 ID，不输出姓名。
     * 未传接收人时替换成空字符串。
     */
    private String formatReceivers(Receivers receivers) {
        if (receivers == null || receivers.empty()) {
            return "";
        }
        return "userIds=" + receivers.userIds() + ",deptIds=" + receivers.deptIds();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /** 示例占位。落地时按配置的 url、method、headers 发送 body。 */
    private int post(String body) {
        return 200;
    }
}
