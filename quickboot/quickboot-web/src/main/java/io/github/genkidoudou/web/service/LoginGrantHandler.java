package io.github.genkidoudou.web.service;

import io.github.genkidoudou.common.security.vo.LoginUser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录策略
 *
 * @author luyanan
 * @since 2026/9/18
 */
public interface LoginGrantHandler {

    String grantType();


    /**
     * 用户授权
     *
     * @param request 请求
     * @return
     * @since 2026/9/18
     */
    LoginUser authenticate(HttpServletRequest request);
}
