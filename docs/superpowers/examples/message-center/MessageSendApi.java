package io.github.genkidoudou.common.message;

/**
 * 业务模块使用的发送入口。
 * <p>
 * 示例代码，不参与编译。接口放在 common，实现放在 quickboot-message。
 * system、job 只依赖这个接口，不依赖 message 包，避免和「消息模块要查用户」循环依赖。
 * <p>
 * 实现必须把来源写成 API。管理端手工发送不走这个方法。
 */
public interface MessageSendApi {

    /**
     * 同步发送。返回时每条通道都已有终态。
     * <p>
     * bizUrl 不合法时抛出参数异常，不落发送单。
     * 通道不存在、停用、站内信没有接收人，都不抛到调用方，而是写在对应明细里。
     *
     * @param command 标题、正文、通道和可选的本次接收人、业务地址
     * @return 发送单 ID、总状态和每条明细
     */
    MessageSendResult send(MessageSendCommand command);
}
