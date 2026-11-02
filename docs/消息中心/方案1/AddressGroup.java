import java.util.List;

/**
 * 群组是一份最终地址，顺序就是投递顺序。
 * 它只标明通道类型，不记录某一条通道的 ID。
 */
public record AddressGroup(long id, String name, ChannelType type, List<String> addresses) {
}
