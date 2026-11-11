package io.github.genkidoudou.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 注册 Sa-Token 登录拦截，并按 {@code sa-token.ignore-url} 放行 Ant 路径。
 * <p>
 * 放行同时使用：
 * <ul>
 *   <li>{@code excludePathPatterns}：整条拦截器不进入这些路径</li>
 *   <li>{@code SaRouter.notMatch}：路由规则层再排除一层，避免漏配</li>
 * </ul>
 * 路径支持 {@code /**}、{@code /api/**} 等 Ant 风格。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(SaInterceptor.class)
@EnableConfigurationProperties(SaTokenIgnoreProperties.class)
public class SaTokenWebMvcAutoConfiguration implements WebMvcConfigurer {

    private final SaTokenIgnoreProperties ignoreProperties;

    public SaTokenWebMvcAutoConfiguration(SaTokenIgnoreProperties ignoreProperties) {
        this.ignoreProperties = ignoreProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> ignoreUrls = ignoreProperties.resolvedIgnoreUrls();
        registry.addInterceptor(new SaInterceptor(handler ->
                        SaRouter.match("/**")
                                .notMatch(ignoreUrls)
                                .check(StpUtil::checkLogin)))
                .addPathPatterns("/**")
                .excludePathPatterns(ignoreUrls);
    }
}
