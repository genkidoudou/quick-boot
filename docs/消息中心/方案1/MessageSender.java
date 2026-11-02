import java.util.List;

/**
 * 统一发送入口。对内对外都走这一个方法。
 *
 * <p>addresses 为 null 表示没传地址列表；空列表表示传了，但是空的。
 * groupId 为 null 表示没传群组。两者不能同时有值。
 * 调用方若拿到的群组 ID 是空白字符串，应先换成 null 再进来。
 *
 * <p>需要收件人的通道：邮件、短信、站内信、企业微信应用。
 * 机器人不接受地址和群组，回执只有一行，地址是通道 ID 的十进制字符串。
 */
public interface MessageSender {

    SendResult send(long channelId, List<String> addresses, Long groupId, SendMessage message);
}
