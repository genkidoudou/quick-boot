package io.github.genkidoudou.web.satoken;

/**
 * 原 Sa-Token 拦截注册已迁移到 common 自动配置：
 * {@link io.github.genkidoudou.satoken.SaTokenWebMvcAutoConfiguration}。
 * <p>
 * 匿名路径请配置 {@code sa-token.ignore-url}（支持 {@code /**}、{@code /api/**}）。
 * 本类保留为空壳，避免旧引用找不到类型；不再注册拦截器。
 */
public final class SaTokenConfigure {

    private SaTokenConfigure() {
    }
}
