package docs.example.auth;

import cn.dev33.satoken.annotation.SaIgnore;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.exception.WarningException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 示例：登录页引导接口。前端据此显示「密码 / 短信」Tab 与是否要验证码。
 * <p>
 * 必须 {@code @SaIgnore}（或加入 ignore-url）。不要在此接口要求 Client Basic。
 */
@RestController
@RequestMapping("/oauth/client")
@RequiredArgsConstructor
public class LoginMetaControllerExample {

    private final LoginGrantDispatcherExample.AppClientQuery appClientQuery;

    @SaIgnore
    @GetMapping("/{clientId}/login-meta")
    public R<Map<String, Object>> loginMeta(@PathVariable String clientId) {
        ClientGrantSupport.AppClient client = appClientQuery.findByClientId(clientId);
        if (client == null || !client.enabled()) {
            throw new WarningException(7011);
        }

        // 人机登录相关的 grant；过滤掉 refresh_token / client_credentials 以免干扰 Tab
        List<String> loginGrants = new ArrayList<>();
        for (String g : client.grantTypeSet()) {
            if ("password".equals(g) || "sms".equals(g)) {
                loginGrants.add(g);
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("clientId", client.clientId());
        body.put("clientName", client.clientName());
        body.put("grantTypes", loginGrants);
        body.put("checkCaptcha", client.checkCaptcha());
        return R.ok(body);
    }
}
