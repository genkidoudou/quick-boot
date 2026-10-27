package io.github.genkidoudou.common.message;

/**
 * 一个通道实现返回的终态。
 * <p>
 * 示例代码，不参与编译。SKIPPED 通常由编排层直接写入，不由通道实现返回。
 * 实现只返回成功或失败。爱语飞飞任一 token 失败时，返回失败而不是部分成功。
 *
 * @param targetDigest 写入明细的脱敏摘要，如「86 人」或「Webhook 末尾 a3f9」
 * @param errorMsg     失败原因。成功时为空
 */
public record MessageChannelResult(String targetDigest, String errorMsg) {

    /**
     * @param targetDigest 脱敏后的目标摘要
     * @return 成功结果
     */
    public static MessageChannelResult success(String targetDigest) {
        return new MessageChannelResult(targetDigest, null);
    }

    /**
     * @param targetDigest 脱敏后的目标摘要
     * @param errorMsg     给发送明细展示的原因
     * @return 失败结果
     */
    public static MessageChannelResult fail(String targetDigest, String errorMsg) {
        return new MessageChannelResult(targetDigest, errorMsg);
    }

    /**
     * @return 没有错误信息时视为成功
     */
    public boolean success() {
        return errorMsg == null || errorMsg.isBlank();
    }
}
