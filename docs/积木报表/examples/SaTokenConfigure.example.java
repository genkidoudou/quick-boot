package docs.jimureport.example;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截示例：放行积木路径，鉴权交给 JmReportTokenServiceI。
 * <p>
 * 落地位置建议：{@code quickboot-web/.../satoken/SaTokenConfigure.java}
 * <p>
 * 为什么要放行？
 * <ul>
 *   <li>积木前端会请求 {@code /jmreport/**}、{@code /drag/**}、{@code /jimubi/**}</li>
 *   <li>这些请求习惯带积木自己的 token（查询参数 / 头），不是每一步都走你们的 Bearer 过滤器逻辑</li>
 *   <li>若全局 {@code SaInterceptor} 强制登录，iframe 第一跳 HTML 就可能 401，页面空白</li>
 * </ul>
 * 放行 ≠ 匿名可读数据。真正门禁在 {@code JmReportTokenServiceI#verifyToken}。
 */
@Configuration
public class SaTokenConfigureExample implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 业务接口：必须登录
                    SaRouter.match("/**")
                            // 积木报表设计器 / 预览 / 静态资源
                            .notMatch("/jmreport/**")
                            // 积木 BI 大屏 / 仪表盘
                            .notMatch("/drag/**")
                            .notMatch("/jimubi/**")
                            // 下面按你们现有白名单继续 notMatch（登录、验证码、actuator 等）
                            // .notMatch("/login")
                            // .notMatch("/auth/**")
                            .check(StpUtil::checkLogin);
                }))
                .addPathPatterns("/**");
    }
}
