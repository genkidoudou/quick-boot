package io.github.genkidoudou.system.internal.message;

/**
 * 消息发送用到的枚举。示例，不参与编译。
 */
public final class MessageTypes {

    private MessageTypes() {
    }

    /** 渠道类型。一条消息最多一个站内渠道。 */
    public enum ChannelType {
        /** 写收件箱。不使用 Webhook 地址和 Bean 名称。 */
        IN_APP,
        /** 把渲染结果 POST 一次。 */
        WEBHOOK,
        /** 按名称调用 {@code MessageSender}。 */
        BEAN
    }

    /** 渠道声明的正文格式，同时抄进渠道快照。 */
    public enum ContentType {
        TEXT,
        HTML,
        JSON,
        MARKDOWN
    }

    /**
     * 消息总状态。
     * 全部渠道成功是 SUCCESS，有成功也有失败是 PARTIAL，没有收件人或全部渠道失败是 FAILED。
     */
    public enum MessageStatus {
        SUCCESS,
        PARTIAL,
        FAILED
    }

    /** 单个渠道快照的投递结果。 */
    public enum DeliveryStatus {
        SUCCESS,
        FAILED
    }

    /** 消息从哪来。外部 token 调用记 API，管理端记 ADMIN。 */
    public enum MessageSource {
        ADMIN,
        API
    }
}
