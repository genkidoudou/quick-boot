package io.github.genkidoudou.system.internal.message;

import java.util.List;
import java.util.Map;

/**
 * 只服务模板替换：把对象和数组变成 JSON 文本。示例，不参与编译。
 * <p>
 * 落地时换成项目已有的 JSON 工具。这里写出来是为了把「字符串不加引号、对象要变成 JSON」这两件事分开。
 */
final class JsonTexts {

    private JsonTexts() {
    }

    static String of(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String text) {
            return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringBuilder out = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    out.append(',');
                }
                first = false;
                out.append(of(String.valueOf(entry.getKey()))).append(':').append(of(entry.getValue()));
            }
            return out.append('}').toString();
        }
        if (value instanceof List<?> list) {
            StringBuilder out = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    out.append(',');
                }
                out.append(of(list.get(i)));
            }
            return out.append(']').toString();
        }
        return of(String.valueOf(value));
    }
}
