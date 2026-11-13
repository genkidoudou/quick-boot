package io.github.genkidoudou.common.satoken;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 匿名放行路径，绑定 {@code sa-token.ignore-url}。
 * <p>
 * 支持 Ant 风格：{@code /**}、{@code /api/**}、{@code /jimubi/**} 等。
 * 配置后由 {@link SaTokenWebMvcAutoConfiguration} 在拦截器中排除登录校验。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sa-token")
public class SaTokenIgnoreProperties {

    /**
     * 未登录可访问的 URL 列表（Ant Path）。
     * <pre>
     * sa-token:
     *   ignore-url:
     *     - /login
     *     - /api/captcha/**
     *     - /jimubi/**
     * </pre>
     */
    private List<String> ignoreUrl = new ArrayList<>();

    /**
     * 清洗后的放行列表：去空白、去重，保持配置顺序。
     *
     * @return 可用于 {@code excludePathPatterns} / {@code SaRouter.notMatch} 的路径
     */
    public List<String> resolvedIgnoreUrls() {
        List<String> result = new ArrayList<>();
        if (ignoreUrl == null || ignoreUrl.isEmpty()) {
            return result;
        }
        for (String raw : ignoreUrl) {
            if (raw == null) {
                continue;
            }
            String path = raw.trim();
            if (path.isEmpty() || result.contains(path)) {
                continue;
            }
            result.add(path);
        }
        return result;
    }
}
