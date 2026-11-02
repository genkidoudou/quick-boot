/** 七种通道。群组只能使用前四种。 */
public enum ChannelType {
    EMAIL,
    SMS,
    INBOX,
    WECOM_APP,
    DINGTALK_ROBOT,
    WECOM_ROBOT,
    IYUU;

    /** 钉钉机器人、企业微信机器人、IYUU。地址在通道配置里。 */
    public boolean robot() {
        return this == DINGTALK_ROBOT || this == WECOM_ROBOT || this == IYUU;
    }
}
