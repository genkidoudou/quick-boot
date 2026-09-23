package io.github.genkidoudou.common.message;

/**
 * 消息发送通道 SPI：各通道（站内信、IYUU、短信、邮件等）实现本接口并由业务模块注册为 Spring Bean。
 * <p>
 * 约束：实现类不应放在 common；common 仅定义契约。调用方按 {@link #channelType()} 路由，
 * 单次失败不应假定会中断其它通道（由编排层决定）。
 */
public interface MessageChannel {

  /**
   * @return 本实现对应的通道类型
   */
  ChannelType channelType();

  /**
   * 执行一次投递。
   *
   * @param request 投递请求
   * @return 投递结果（SUCCESS / FAIL / SKIPPED）
   */
  MessageChannelResult send(MessageChannelRequest request);
}
