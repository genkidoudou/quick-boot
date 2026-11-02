/**
 * 整单拒绝的原因。命中任意一条时没有回执。
 * 这些字符串就是返回给调用方的原因，不要再包一层别的文案。
 */
public final class RejectReason {

    public static final String CHANNEL_NOT_FOUND = "通道不存在";
    public static final String CHANNEL_DISABLED = "通道已停用";
    public static final String MESSAGE_INCOMPLETE = "标题或正文为空";
    public static final String ADDRESS_AND_GROUP = "地址列表和群组不能同时传入";
    public static final String RECIPIENT_REQUIRED = "缺少收件人";
    public static final String ROBOT_REJECTS_RECIPIENT = "机器人不能带收件人";
    public static final String GROUP_NOT_FOUND = "群组不存在";
    public static final String GROUP_TYPE_MISMATCH = "群组类型与通道不符";
    public static final String GROUP_EMPTY = "群组为空";

    /** 单行失败，而且没有把这个地址交给供应商。 */
    public static final String BAD_ADDRESS = "地址格式不正确";

    private RejectReason() {
    }
}
