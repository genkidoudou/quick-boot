import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 邮件、短信、站内信、企业微信应用。
 * 每个地址单独占一行回执。这里用 Outbound 代替真实的 SMTP、短信和企业微信 HTTP。
 */
public final class RecipientAdapters {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]+$");

    private RecipientAdapters() {
    }

    public static ChannelAdapter email(Outbound outbound) {
        return new Email(outbound);
    }

    public static ChannelAdapter sms(Outbound outbound) {
        return new Sms(outbound);
    }

    public static ChannelAdapter inbox(InboxStore inbox) {
        return new Inbox(inbox);
    }

    public static ChannelAdapter wecomApp(Outbound outbound) {
        return new WecomApp(outbound);
    }

    /** 主题是标题，正文是纯文本。发件人取通道配置，不来自消息体。 */
    private static final class Email implements ChannelAdapter {
        private final Outbound outbound;

        private Email(Outbound outbound) {
            this.outbound = outbound;
        }

        public ChannelType type() {
            return ChannelType.EMAIL;
        }

        public List<AddressReceipt> deliver(Channel channel, List<String> addresses, SendMessage message) {
            return Delivery.each(addresses, EMAIL.asMatchPredicate(), outbound, Payload.email(message));
        }
    }

    /** 只把正文和可选链接交给供应商。标题被丢掉。 */
    private static final class Sms implements ChannelAdapter {
        private final Outbound outbound;

        private Sms(Outbound outbound) {
            this.outbound = outbound;
        }

        public ChannelType type() {
            return ChannelType.SMS;
        }

        public List<AddressReceipt> deliver(Channel channel, List<String> addresses, SendMessage message) {
            return Delivery.each(addresses, PHONE.asMatchPredicate(), outbound, Payload.sms(message));
        }
    }

    /**
     * 地址是用户 ID，不查这个用户在不在。
     * 写入成功即送达。未传链接时 message.link() 为 null，表里的 link 留空。
     */
    private static final class Inbox implements ChannelAdapter {
        private final InboxStore inbox;

        private Inbox(InboxStore inbox) {
            this.inbox = inbox;
        }

        public ChannelType type() {
            return ChannelType.INBOX;
        }

        public List<AddressReceipt> deliver(Channel channel, List<String> addresses, SendMessage message) {
            List<AddressReceipt> rows = new ArrayList<>();
            for (String raw : addresses) {
                String address = raw == null ? "" : raw.trim();
                if (address.isEmpty()) {
                    rows.add(AddressReceipt.fail(address, RejectReason.BAD_ADDRESS));
                    continue;
                }
                try {
                    inbox.save(channel.id(), address, message);
                    rows.add(AddressReceipt.ok(address));
                } catch (RuntimeException ex) {
                    rows.add(AddressReceipt.fail(address, ex.getMessage()));
                }
            }
            return rows;
        }
    }

    /** 文本是标题、空行、正文，有链接再空一行。地址是企业微信 ID。 */
    private static final class WecomApp implements ChannelAdapter {
        private final Outbound outbound;

        private WecomApp(Outbound outbound) {
            this.outbound = outbound;
        }

        public ChannelType type() {
            return ChannelType.WECOM_APP;
        }

        public List<AddressReceipt> deliver(Channel channel, List<String> addresses, SendMessage message) {
            return Delivery.each(addresses, address -> !address.isEmpty(), outbound, Payload.wecomApp(message));
        }
    }
}
