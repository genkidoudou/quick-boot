import java.util.List;

/** 一种通道类型一个适配器。适配器不读取群组。 */
interface ChannelAdapter {

    ChannelType type();

    List<AddressReceipt> deliver(Channel channel, List<String> addresses, SendMessage message);
}
