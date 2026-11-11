package docs.example.auth;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.security.vo.LoginUser;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 示例：应用客户端 + grant_types 校验辅助。
 * <p>
 * 落地时：把「按 clientId 查库」换成你们的 SysOauthClientApi / Mapper；
 * 异常码换成项目 ErrorCodes。
 */
public final class ClientGrantSupport {

    private ClientGrantSupport() {
    }

    /** 登录请求解析出的客户端视图（示例 DTO，非生产实体）。 */
    public record AppClient(
            String clientId,
            String clientName,
            /** public | confidential */
            String clientType,
            /** 原始串，如 password,sms */
            String grantTypesRaw,
            /** 是否校验验证码：与表字段 check_captcha 对齐，"1"=是 */
            boolean checkCaptcha,
            /** 可空：秒；空则用全局 Sa-Token 超时 */
            Integer tokenTimeout,
            /** 是否启用 */
            boolean enabled
    ) {
        /** 解析为小写 grant 集合。 */
        public Set<String> grantTypeSet() {
            if (StrUtil.isBlank(grantTypesRaw)) {
                return Set.of();
            }
            return Arrays.stream(grantTypesRaw.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());
        }
    }

    /**
     * 解析 clientId：优先参数 clientId / client_id；兼容期可默认 quick-ui。
     *
     * @param clientIdFromRequest 请求里的 clientId
     * @param compatDefault       兼容期默认值；兼容关闭后传 null 并强制必填
     */
    public static String requireClientId(String clientIdFromRequest, String compatDefault) {
        String id = StrUtil.trimToEmpty(clientIdFromRequest);
        if (StrUtil.isNotBlank(id)) {
            return id;
        }
        if (StrUtil.isNotBlank(compatDefault)) {
            return compatDefault;
        }
        // 示例：客户端不能为空
        throw new WarningException(7010);
    }

    /**
     * 校验：客户端启用，且申请的 grantType 在 grant_types 内。
     *
     * @param client    已查库的客户端
     * @param grantType 请求的 grant（password / sms …）
     */
    public static void assertGrantAllowed(AppClient client, String grantType) {
        if (client == null || !client.enabled()) {
            throw new WarningException(7011); // 客户端不存在或停用
        }
        String type = StrUtil.trimToEmpty(grantType).toLowerCase(Locale.ROOT);
        if (StrUtil.isBlank(type) || !client.grantTypeSet().contains(type)) {
            throw new WarningException(7012); // 该客户端不允许此 grant_type
        }
        // public 客户端禁止走 client_credentials（即使误配进 grant_types 也挡一层）
        if ("client_credentials".equals(type) && "public".equalsIgnoreCase(client.clientType())) {
            throw new WarningException(7013);
        }
    }

    /** 登录成功后把 clientId 写入会话用户。 */
    public static void bindClientId(LoginUser loginUser, String clientId) {
        if (loginUser != null) {
            loginUser.setClientId(clientId);
        }
    }
}
