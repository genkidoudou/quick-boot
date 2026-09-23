package io.github.genkidoudou.common.message;

import lombok.Builder;
import lombok.Data;

/**
 * 单次通道投递请求：由编排层按「接收人 × 渠道」展开后交给 {@link MessageChannel}。
 */
@Data
@Builder
public class MessageChannelRequest {

  /** 通道类型。 */
  private ChannelType channelType;

  /** 消息标题。 */
  private String title;

  /** 消息正文。 */
  private String content;

  /**
   * 实际投递目标：用户 ID 字符串、IYUU token、手机号或邮箱等。
   */
  private String target;

  /**
   * 接收人类型提示：{@code USER} / {@code CONTACT}（通道可选使用）。
   */
  private String recipientType;

  /** 接收人业务主键（用户 ID 或联系人 ID）。 */
  private Long recipientId;
}
