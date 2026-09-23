package io.github.genkidoudou.web.service;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.ErrorException;
import io.github.genkidoudou.common.security.vo.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 按 grantType 分发到对应 {@link LoginGrantHandler}。 */
@Component
public class LoginGrantDispatcher {

    private final Map<String, LoginGrantHandler> handlers;

    public LoginGrantDispatcher(List<LoginGrantHandler> list) {
        this.handlers = list.stream()
                .collect(Collectors.toMap(h -> h.grantType().toLowerCase(), Function.identity()));
    }

    public LoginUser authenticate(HttpServletRequest request) {
        String grantType = request.getParameter("grantType");
        if (StrUtil.isBlank(grantType)) {
            throw new ErrorException(7001);
        }
        String type = grantType.toLowerCase();
        LoginGrantHandler handler = handlers.get(type);
        if (handler == null) {
            throw new ErrorException(7001);
        }
        return handler.authenticate(request);
    }
}
