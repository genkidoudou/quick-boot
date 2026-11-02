/** 把统一消息体变成各通道实际发出的文本。链接为 null 时不追加空行。 */
public final class Payload {

    private Payload() {
    }

    /** 邮件正文、机器人 Markdown 正文、IYUU 通知内容：正文后换行再写链接。 */
    public static String textWithLink(SendMessage message) {
        if (message.link() == null) {
            return message.body();
        }
        return message.body() + "\n" + message.link();
    }

    /** 短信只发这段，标题不进入短信。 */
    public static Rendered sms(SendMessage message) {
        return new Rendered(null, textWithLink(message));
    }

    /** 邮件主题是标题。 */
    public static Rendered email(SendMessage message) {
        return new Rendered(message.title(), textWithLink(message));
    }

    /**
     * 企业微信应用文本：标题、空行、正文。有链接时再空一行写链接。
     */
    public static Rendered wecomApp(SendMessage message) {
        String text = message.title() + "\n\n" + message.body();
        if (message.link() != null) {
            text = text + "\n\n" + message.link();
        }
        return new Rendered(null, text);
    }

    /** 钉钉和企业微信机器人的 Markdown，以及 IYUU 的标题加内容。 */
    public static Rendered robot(SendMessage message) {
        return new Rendered(message.title(), textWithLink(message));
    }
}
