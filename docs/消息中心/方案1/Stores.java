/** 按主键读取通道。不存在返回 null。 */
interface ChannelStore {
    Channel findChannel(long id);
}

/** 按主键读取群组及其地址，地址已按 sort_no 排好。不存在返回 null。 */
interface GroupStore {
    AddressGroup findGroup(long id);
}

/** 站内信的投递就是写入一行。失败时抛出 RuntimeException，message 成为该行原因。 */
interface InboxStore {
    void save(long channelId, String userAddress, SendMessage message);
}
