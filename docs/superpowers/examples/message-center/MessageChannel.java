package io.github.genkidoudou.common.message;

/**
 * 单次通道投递契约。
 * <p>
 * 示例代码，不参与编译。六种内置类型各有一个实现，注册为 Spring Bean，放在 message 模块内部。
 * 编排层用 channelType() 选择实现。某一个实现抛异常时，编排层捕获并继续其他通道。
 */
public interface MessageChannel {

    /**
     * @return 本实现负责的类型。同一类型只应有一个实现；多条通道实例靠配置区分，不靠多个实现类
     */
    ChannelType channelType();

    /**
     * 执行一次投递。
     * <p>
     * 站内信实现负责展开用户并写收件箱。机器人实现负责一次 HTTP。
     * 实现不决定其他通道是否继续。
     *
     * @param request 已经按通道配置和本次接收人准备好的请求
     * @return 这一条通道的终态
     */
    MessageChannelResult send(MessageChannelRequest request);
}
