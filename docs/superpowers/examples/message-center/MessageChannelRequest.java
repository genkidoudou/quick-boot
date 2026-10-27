package io.github.genkidoudou.common.message;

/**
 * 交给某一个通道实现的一次投递参数。
 * <p>
 * 示例代码，不参与编译。编排层先判断通道是否存在、是否停用，再构造本对象。
 * 因此实现里看到的通道都是启用的。站内信接收人为空的情况也由编排层事先写成跳过，不进入实现。
 *
 * @param sendId        发送单 ID。站内信写入 msg_inbox.send_id。其他通道可以不使用
 * @param channelId     通道 UUID
 * @param channelConfig 该通道的 JSON 配置原文
 * @param title         标题
 * @param content       正文
 * @param receivers     站内信使用的接收人，部门已展开为 userIds。机器人通道忽略它，不拿它替换 Webhook
 * @param audience      CHANNEL 或 REQUEST。只有站内信有值
 * @param bizUrl        原样传给站内信快照、Webhook 模板和 Bean。机器人正文不自动拼接
 */
public record MessageChannelRequest(
        Long sendId,
        String channelId,
        String channelConfig,
        String title,
        String content,
        MessageSendCommand.Receivers receivers,
        String audience,
        String bizUrl) {
}
