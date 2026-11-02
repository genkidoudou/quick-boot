import java.util.List;

/**
 * 三种机器人。调用方不传地址。
 * 回执的 address 使用通道 ID，因为真正的 Webhook 或令牌在通道配置里。
 * 钉钉还要使用 webhookSecret 做加签；企业微信机器人只有 webhookUrl；IYUU 使用 iyuuToken。
 * 示例不发起 HTTP，只把标题和正文交给 Outbound。
 */
public final class RobotAdapters {

    private RobotAdapters() {
    }

    public static ChannelAdapter dingtalk(Outbound outbound) {
        return new Robot(ChannelType.DINGTALK_ROBOT, outbound);
    }

    public static ChannelAdapter wecomRobot(Outbound outbound) {
        return new Robot(ChannelType.WECOM_ROBOT, outbound);
    }

    public static ChannelAdapter iyuu(Outbound outbound) {
        return new Robot(ChannelType.IYUU, outbound);
    }

    private static final class Robot implements ChannelAdapter {
        private final ChannelType type;
        private final Outbound outbound;

        private Robot(ChannelType type, Outbound outbound) {
            this.type = type;
            this.outbound = outbound;
        }

        public ChannelType type() {
            return type;
        }

        public List<AddressReceipt> deliver(Channel channel, List<String> addresses, SendMessage message) {
            String address = Long.toString(channel.id());
            Rendered rendered = Payload.robot(message);
            try {
                outbound.connect();
                outbound.send(address, rendered);
                return List.of(AddressReceipt.ok(address));
            } catch (RuntimeException ex) {
                return List.of(AddressReceipt.fail(address, ex.getMessage()));
            }
        }
    }
}
