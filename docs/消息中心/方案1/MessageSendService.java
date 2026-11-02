import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 方案 1 的发送实现。检查顺序固定，命中第一条就整单拒绝，不返回回执。
 * 投递开始之后不再改成整单拒绝。
 */
public final class MessageSendService implements MessageSender {

    private final ChannelStore channels;
    private final GroupStore groups;
    private final Map<ChannelType, ChannelAdapter> adapters;

    public MessageSendService(ChannelStore channels, GroupStore groups, List<ChannelAdapter> adapters) {
        this.channels = channels;
        this.groups = groups;
        this.adapters = new EnumMap<>(ChannelType.class);
        for (ChannelAdapter adapter : adapters) {
            this.adapters.put(adapter.type(), adapter);
        }
    }

    @Override
    public SendResult send(long channelId, List<String> addresses, Long groupId, SendMessage message) {
        Channel channel = channels.findChannel(channelId);
        if (channel == null) {
            return SendResult.reject(RejectReason.CHANNEL_NOT_FOUND);
        }
        if (!channel.enabled()) {
            return SendResult.reject(RejectReason.CHANNEL_DISABLED);
        }

        String title = blankToNull(message.title());
        String body = blankToNull(message.body());
        if (title == null || body == null) {
            return SendResult.reject(RejectReason.MESSAGE_INCOMPLETE);
        }
        SendMessage normalized = new SendMessage(title, body, blankToNull(message.link()));

        boolean addressesGiven = addresses != null;
        boolean groupGiven = groupId != null;
        if (addressesGiven && groupGiven) {
            return SendResult.reject(RejectReason.ADDRESS_AND_GROUP);
        }
        if (channel.type().robot() && (addressesGiven || groupGiven)) {
            return SendResult.reject(RejectReason.ROBOT_REJECTS_RECIPIENT);
        }

        List<String> targets;
        if (channel.type().robot()) {
            targets = List.of();
        } else if (groupGiven) {
            AddressGroup group = groups.findGroup(groupId);
            if (group == null) {
                return SendResult.reject(RejectReason.GROUP_NOT_FOUND);
            }
            if (group.type() != channel.type()) {
                return SendResult.reject(RejectReason.GROUP_TYPE_MISMATCH);
            }
            if (group.addresses().isEmpty()) {
                return SendResult.reject(RejectReason.GROUP_EMPTY);
            }
            targets = group.addresses();
        } else if (!addressesGiven || addresses.isEmpty()) {
            return SendResult.reject(RejectReason.RECIPIENT_REQUIRED);
        } else {
            targets = addresses;
        }

        ChannelAdapter adapter = adapters.get(channel.type());
        return SendResult.ok(adapter.deliver(channel, targets, normalized));
    }

    /** 空白视为没传。群组 ID 的空白字符串应在进入本方法前变成 null。 */
    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
