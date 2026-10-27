package io.github.genkidoudou.common.message;

/**
 * 内部 Bean 通道的扩展点。
 * <p>
 * 示例代码，不参与编译。业务模块把实现注册成 Spring Bean 即可，不必依赖 message 包。
 * 编排层按通道配置里的 beanName 取 Bean，并且只用 instanceof 确认是本接口。
 * 取到其他类型时明细记失败，不会反射调用任意方法。
 */
public interface MessageBeanSender {

    /**
     * 发送一次。抛出的异常由编排层收成该通道的 FAIL，不影响其他通道。
     *
     * @param context 本次标题、正文、通道参数，以及可选的接收人和业务地址
     */
    void send(Context context);

    /**
     * @param channelId 通道 UUID
     * @param params    通道配置里的 params 原文，可空
     * @param title     标题
     * @param content   正文
     * @param receivers 接口传入的接收人。未传时为 null。Bean 可以读，但本设计不要求它据此改变外部地址
     * @param bizUrl    业务地址，可空
     */
    record Context(
            String channelId,
            String params,
            String title,
            String content,
            MessageSendCommand.Receivers receivers,
            String bizUrl) {
    }
}
