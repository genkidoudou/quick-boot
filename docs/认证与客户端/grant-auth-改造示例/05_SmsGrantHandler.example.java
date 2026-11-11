package docs.example.auth;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.web.service.AbstractLoginGrantHandler;
import io.github.genkidoudou.web.service.LoginGrantHandler;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 示例：扩展 grant_type = sms（手机号 + 短信验证码）。
 * <p>
 * 仅当客户端 grant_types 包含 {@code sms} 时，Dispatcher 才会放行到本 Handler。
 * 短信码校验、限流、防刷请接你们的消息/验证码基础设施；此处只演示形状。
 */
public class SmsGrantHandlerExample extends AbstractLoginGrantHandler implements LoginGrantHandler {

    public SmsGrantHandlerExample(io.github.genkidoudou.system.api.api.SysMenuApi sysMenuApi,
                                  io.github.genkidoudou.system.api.api.SysUserApi sysUserApi) {
        super(sysMenuApi, sysUserApi);
    }

    @Override
    public String grantType() {
        return "sms";
    }

    @Override
    public LoginUser authenticate(HttpServletRequest request) {
        String mobile = firstNonBlank(request.getParameter("mobile"), request.getParameter("phone"));
        String smsCode = firstNonBlank(request.getParameter("smsCode"), request.getParameter("sms_code"));
        if (StrUtil.hasBlank(mobile, smsCode)) {
            throw new WarningException(7002);
        }

        ClientGrantSupport.AppClient client =
                (ClientGrantSupport.AppClient) request.getAttribute(LoginGrantDispatcherExample.AppClientQuery.REQUEST_ATTR);
        if (client != null && client.checkCaptcha()) {
            // 与 password 相同：行为验证码（天爱等）——可选；短信码本身已是一重因子
            // verifyCaptcha(request);
        }

        // 1) 校验短信码（Redis / 消息中心）
        // 2) 按手机号查 sys_user
        // 3) buildLoginUser
        throw new UnsupportedOperationException("示例：接短信校验与 findByMobile 后返回 LoginUser");
    }

    private static String firstNonBlank(String a, String b) {
        return StrUtil.isNotBlank(a) ? a : b;
    }
}
