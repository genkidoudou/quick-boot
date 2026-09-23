package io.github.genkidoudou.common.message;

import lombok.Builder;
import lombok.Data;

/**
 * 单次通道投递结果。
 */
@Data
@Builder
public class MessageChannelResult {

  /** 明细状态：SUCCESS / FAIL / SKIPPED。 */
  private String status;

  /** 失败或跳过原因；成功时可空。 */
  private String errorMsg;

  /** 通道回写的实际目标（如脱敏前完整 target，由编排层决定是否落库）。 */
  private String target;

  /**
   * 构造成功结果。
   *
   * @param target 实际目标
   * @return 结果
   */
  public static MessageChannelResult success(String target) {
    return MessageChannelResult.builder().status("SUCCESS").target(target).build();
  }

  /**
   * 构造失败结果。
   *
   * @param target   实际目标
   * @param errorMsg 原因
   * @return 结果
   */
  public static MessageChannelResult fail(String target, String errorMsg) {
    return MessageChannelResult.builder().status("FAIL").target(target).errorMsg(errorMsg).build();
  }

  /**
   * 构造跳过结果（Stub 通道）。
   *
   * @param target   目标
   * @param errorMsg 说明
   * @return 结果
   */
  public static MessageChannelResult skipped(String target, String errorMsg) {
    return MessageChannelResult.builder().status("SKIPPED").target(target).errorMsg(errorMsg).build();
  }
}
