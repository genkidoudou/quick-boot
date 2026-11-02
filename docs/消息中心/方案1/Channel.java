/**
 * 发送层读到的通道。配置里只有这种类型用得到的连接参数，没有收件人。
 */
public record Channel(long id, String name, ChannelType type, boolean enabled, ChannelConfig config) {
}
