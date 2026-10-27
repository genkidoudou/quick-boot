package io.github.genkidoudou.common.message;

/**
 * 消息通道类型。
 * <p>
 * 示例代码，放在 docs 下，不参与编译。落地时替换 common 里现有枚举，不保留 SMS、MAIL。
 * 管理端字典 MSG_CHANNEL_TYPE 的取值与这里的 name() 一致。
 */
public enum ChannelType {

    /** 站内信。展开为每个用户一行收件箱，并记录已读。 */
    INBOX,

    /** 钉钉自定义机器人。投递地址是通道上的 Webhook。 */
    DINGTALK_ROBOT,

    /** 企业微信群机器人。投递地址是通道上的 Webhook。 */
    WECOM_ROBOT,

    /** 爱语飞飞。通道上配置一个或多个 token，明细仍只记一行。 */
    IYUU,

    /** 自定义 HTTP。正文模板可读取本次接收人和业务地址。 */
    WEBHOOK,

    /** 内部 Bean。只能调用 MessageBeanSender，不能按名字调用任意 Bean。 */
    BEAN
}
