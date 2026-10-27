package io.github.genkidoudou.common.message;

import java.util.List;

/**
 * 一次发送的返回值。
 * <p>
 * 示例代码，不参与编译。调用返回时投递已经结束，明细状态是 SUCCESS、FAIL 或 SKIPPED。
 * 没有抛异常只说明发送单已处理完，不说明每一条通道都成功。调用方要看 status 和 details。
 *
 * @param sendId  发送单雪花 ID
 * @param status  SUCCESS、PARTIAL 或 FAIL
 * @param details 与请求通道一一对应，顺序与去重后的 channelIds 一致
 */
public record MessageSendResult(
        Long sendId,
        String status,
        List<Detail> details) {

    /**
     * @param channelId 请求里的通道 ID。通道不存在时仍回传该 ID，方便调用方对照
     * @param status    SUCCESS、FAIL 或 SKIPPED
     * @param errorMsg  失败或跳过原因。成功时为空
     */
    public record Detail(String channelId, String status, String errorMsg) {
    }
}
