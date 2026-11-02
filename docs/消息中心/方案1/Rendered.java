/**
 * 适配器交给供应商的报文。title 为 null 表示这种通道不用标题，例如短信。
 */
public record Rendered(String title, String text) {
}
