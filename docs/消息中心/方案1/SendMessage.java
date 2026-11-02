/**
 * 统一消息体。标题和正文必填，链接可以没有。
 * 进入适配器之前，空白标题、正文会被拒绝；只含空白的链接会被当成没传。
 */
public record SendMessage(String title, String body, String link) {
}
