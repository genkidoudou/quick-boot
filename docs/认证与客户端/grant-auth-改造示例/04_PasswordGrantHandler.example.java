package docs.example.auth;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.web.service.AbstractLoginGrantHandler;
import io.github.genkidoudou.web.service.LoginGrantHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;

/**
 * 示例：password grant。
 * <p>
 * 与现网 {@code PasswordGrantHandler} 的差异：
 * <ul>
 *   <li>验证码不再只看全局 {@code captcha.enabled}，优先看客户端 {@code check_captcha}</li>
 *   <li>grant 是否允许已在 Dispatcher 校验，本类只负责凭证</li>
 * </ul>
 * 落地时：改现有 PasswordGrantHandler，不必新建类名。
 */
public class PasswordGrantHandlerExample extends AbstractLoginGrantHandler implements LoginGrantHandler {

    private final ObjectProvider<ImageCaptchaApplication> captchaProvider;

    public PasswordGrantHandlerExample(/* 保持与现网一致的构造依赖 */ ObjectProvider<ImageCaptchaApplication> captchaProvider,
                                       io.github.genkidoudou.system.api.api.SysMenuApi sysMenuApi,
                                       io.github.genkidoudou.system.api.api.SysUserApi sysUserApi) {
        super(sysMenuApi, sysUserApi);
        this.captchaProvider = captchaProvider;
    }

    @Override
    public String grantType() {
        return "password";
    }

    @Override
    public LoginUser authenticate(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (StrUtil.hasBlank(username, password)) {
            throw new WarningException(7002);
        }

        // 按客户端决定是否验证码（示例）；也可：客户端 checkCaptcha OR 全局开关
        ClientGrantSupport.AppClient client =
                (ClientGrantSupport.AppClient) request.getAttribute(LoginGrantDispatcherExample.AppClientQuery.REQUEST_ATTR);
        if (client != null && client.checkCaptcha()) {
            verifyCaptcha(request);
        }

        // ……以下与现网相同：查用户、状态、密码匹配、buildLoginUser ……
        // SysUserVo user = sysUserApi.findByUserName(username);
        // ...
        // return buildLoginUser(user);
        throw new UnsupportedOperationException("示例：省略用户校验，复制现网 PasswordGrantHandler 后半段即可");
    }

    private void verifyCaptcha(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        ImageCaptchaApplication application = captchaProvider.getIfAvailable();
        if (application == null) {
            throw new WarningException(ErrorCodes.Auth.CAPTCHA_SERVICE_UNAVAILABLE);
        }
        if (!(application instanceof SecondaryVerificationApplication secondary)) {
            throw new WarningException(ErrorCodes.Auth.CAPTCHA_SECONDARY_NOT_CONFIGURED);
        }
        if (StrUtil.isBlank(uuid) || !secondary.secondaryVerification(uuid)) {
            throw new WarningException(ErrorCodes.Auth.CAPTCHA_INVALID);
        }
    }
}
