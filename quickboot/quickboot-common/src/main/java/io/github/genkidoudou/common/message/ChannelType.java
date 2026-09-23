package io.github.genkidoudou.common.message;

/**
 * 消息发送通道类型（SPI 契约枚举）。
 * <p>
 * 一期真发：{@link #INBOX}、{@link #IYUU}；{@link #SMS}、{@link #MAIL} 由业务模块提供 Stub 实现。
 */
public enum ChannelType {

  /** 站内信（仅系统用户）。 */
  INBOX,

  /** 爱语飞飞 IYUU 推送。 */
  IYUU,

  /** 短信（一期 Stub）。 */
  SMS,

  /** 邮件（一期 Stub）。 */
  MAIL
}
