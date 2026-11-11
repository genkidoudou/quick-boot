package docs.example.auth;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.ErrorException;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.web.service.LoginGrantHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 示例：在现有 {@code LoginGrantDispatcher} 上增加「客户端 + grant_types」闸门。
 * <p>
 * 现状：只按 grantType 找 Handler。<br>
 * 改造：先解析 clientId → 查 AppClient → assertGrantAllowed → 再 authenticate，并 bindClientId。
 * <p>
 * 请求参数约定（与现网兼容）：
 * <ul>
 *   <li>{@code clientId} 或 {@code client_id}</li>
 *   <li>{@code grantType} 或 {@code grant_type}</li>
 * </ul>
 */
@Component
public class LoginGrantDispatcherExample {

    /** 兼容期默认客户端；上线稳定后改为 null 并强制前端必传。 */
    private static final String COMPAT_DEFAULT_CLIENT = "quick-ui";

    private final Map<String, LoginGrantHandler> handlers;
    /** 示例：换成你们的客户端查询服务 */
    private final AppClientQuery appClientQuery;

    public LoginGrantDispatcherExample(List<LoginGrantHandler> list, AppClientQuery appClientQuery) {
        this.handlers = list.stream()
                .collect(Collectors.toMap(h -> h.grantType().toLowerCase(), Function.identity()));
        this.appClientQuery = appClientQuery;
    }

    public LoginUser authenticate(HttpServletRequest request) {
        String clientId = ClientGrantSupport.requireClientId(
                firstNonBlank(request.getParameter("clientId"), request.getParameter("client_id")),
                COMPAT_DEFAULT_CLIENT);

        String grantType = firstNonBlank(request.getParameter("grantType"), request.getParameter("grant_type"));
        if (StrUtil.isBlank(grantType)) {
            // 兼容期：默认 password；关闭兼容后直接抛 7001
            grantType = "password";
        }
        grantType = grantType.toLowerCase();

        ClientGrantSupport.AppClient client = appClientQuery.findByClientId(clientId);
        ClientGrantSupport.assertGrantAllowed(client, grantType);

        // 把客户端上下文塞进 request，供 Handler 读 checkCaptcha / tokenTimeout
        request.setAttribute(AppClientQuery.REQUEST_ATTR, client);

        LoginGrantHandler handler = handlers.get(grantType);
        if (handler == null) {
            throw new ErrorException(7001);
        }

        LoginUser loginUser = handler.authenticate(request);
        ClientGrantSupport.bindClientId(loginUser, clientId);
        // 若要用客户端 token_timeout：在 LoginHelper.login 前后按 client.tokenTimeout() 设置
        return loginUser;
    }

    private static String firstNonBlank(String a, String b) {
        return StrUtil.isNotBlank(a) ? a : b;
    }

    /** 示例查询接口，落地时删除，改用真实 Api。 */
    public interface AppClientQuery {
        String REQUEST_ATTR = "QC_APP_CLIENT";

        ClientGrantSupport.AppClient findByClientId(String clientId);
    }
}
