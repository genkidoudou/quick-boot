package io.github.genkidoudou.system.internal.message;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 把渠道模板里的 {@code {{name}}} 换成消息变量。示例，不参与编译。
 * <p>
 * 缺一个键，或值是 null，当前渠道失败，其他渠道继续。
 * 占位符必须是两个左花括号紧贴名字、再紧贴两个右花括号。{@code {{ name }}} 这种带空格的不识别。
 */
public final class TemplateRenderer {

    /** 名字只允许英文字母、数字、下划线。 */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([A-Za-z0-9_]+)}}");

    private TemplateRenderer() {
    }

    /**
     * @param template  渠道上的模板正文。调用前已经确认非空白
     * @param variables 消息变量。缺省是空对象，不会是 null
     * @return 成功时带渲染正文；失败时带「缺少变量 xxx」
     */
    public static Rendered render(String template, Map<String, Object> variables) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            if (!variables.containsKey(name) || variables.get(name) == null) {
                return Rendered.failed("缺少变量 " + name);
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(textOf(variables.get(name))));
        }
        matcher.appendTail(out);
        return Rendered.ok(out.toString());
    }

    /**
     * 字符串原样插入，数字和布尔用字面形式，对象和数组用 JSON 文本。
     * 字符串在这里不加引号；它若出现在 JSON 模板里，引号由模板作者写在占位符两边。
     */
    private static String textOf(Object value) {
        if (value instanceof String text) {
            return text;
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> || value instanceof List<?>) {
            return JsonTexts.of(value);
        }
        return String.valueOf(value);
    }

    /** 渲染结果。失败时 content 为空，发送层不投递。 */
    public record Rendered(boolean ok, String content, String error) {

        static Rendered ok(String content) {
            return new Rendered(true, content, null);
        }

        static Rendered failed(String error) {
            return new Rendered(false, null, error);
        }
    }
}
