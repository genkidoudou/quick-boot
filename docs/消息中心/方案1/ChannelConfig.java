/** 与 msg_channel 上的协议列一一对应。用不到的列为 null。 */
public record ChannelConfig(
        String smtpHost,
        Integer smtpPort,
        String smtpUsername,
        String smtpPassword,
        String mailFrom,
        String smsAccessKey,
        String smsSign,
        String smsEndpoint,
        String wecomCorpId,
        String wecomAgentId,
        String wecomSecret,
        String webhookUrl,
        String webhookSecret,
        String iyuuToken) {
}
