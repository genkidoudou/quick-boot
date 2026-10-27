package io.github.genkidoudou.message.internal.channel;

import io.github.genkidoudou.common.message.ChannelType;
import io.github.genkidoudou.common.message.MessageChannel;
import io.github.genkidoudou.common.message.MessageChannelRequest;
import io.github.genkidoudou.common.message.MessageChannelResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 钉钉自定义机器人通道示例。
 * <p>
 * 不参与编译。一次发送只请求该通道配置的 Webhook，不使用 receivers 替换地址。
 * 消息体只有标题和正文。业务地址不自动附在文本末尾；需要链接时让调用方把地址写进正文，
 * 或改用带模板的自定义 Webhook。
 * <p>
 * 企业微信机器人结构相同：读自己的 webhook，POST 文本，失败则这条明细 FAIL。
 */
public class DingtalkRobotChannel implements MessageChannel {

    @Override
    public ChannelType channelType() {
        return ChannelType.DINGTALK_ROBOT;
    }

    @Override
    public MessageChannelResult send(MessageChannelRequest request) {
        DingTalkConfig config = DingTalkConfig.parse(request.channelConfig());
        String url = config.webhook();
        if (config.secret() != null && !config.secret().isBlank()) {
            // 钉钉加签：把 timestamp 和 secret 用 HmacSHA256 签名后拼到 Webhook 查询参数。
            // secret 只存在通道配置里，摘要只保留 Webhook 末尾少量字符。
            url = appendSign(url, config.secret());
        }
        String body = "{\"msgtype\":\"text\",\"text\":{\"content\":"
                + quote(request.title() + "\n" + request.content()) + "}}";
        // 落地时用项目里的 HTTP 客户端执行 POST。非 2xx 或钉钉 errcode 非 0 都返回 fail。
        int status = post(url, body);
        String digest = "Webhook 末尾 " + tail(config.webhook());
        if (status >= 200 && status < 300) {
            return MessageChannelResult.success(digest);
        }
        return MessageChannelResult.fail(digest, "钉钉返回 HTTP " + status);
    }

    /**
     * 按钉钉机器人文档把签名附到地址上。示例只表达参数位置，时钟使用当前毫秒。
     */
    static String appendSign(String webhook, String secret) {
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(Base64.getEncoder().encodeToString(signData), StandardCharsets.UTF_8);
            String joiner = webhook.contains("?") ? "&" : "?";
            return webhook + joiner + "timestamp=" + timestamp + "&sign=" + sign;
        } catch (Exception ex) {
            throw new IllegalStateException("钉钉签名失败", ex);
        }
    }

    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /** 摘要不保存完整地址，避免列表和发送明细泄露 access_token。 */
    private static String tail(String webhook) {
        int length = webhook.length();
        return webhook.substring(Math.max(0, length - 4));
    }

    /** 示例占位。落地时替换为真实 HTTP 调用。 */
    private int post(String url, String body) {
        return 200;
    }

    /**
     * @param webhook 机器人地址
     * @param secret  加签密钥，未加签时为空
     */
    record DingTalkConfig(String webhook, String secret) {
        static DingTalkConfig parse(String json) {
            // 落地时用项目已有的 JSON 工具读取 webhook、secret。
            return new DingTalkConfig(json, null);
        }
    }
}
