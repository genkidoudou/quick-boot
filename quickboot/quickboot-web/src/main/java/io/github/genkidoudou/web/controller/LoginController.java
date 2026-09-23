package io.github.genkidoudou.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.core.security.LoginHelper;
import io.github.genkidoudou.web.service.LoginGrantDispatcher;
import io.github.genkidoudou.web.vo.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 登录
 *
 * @author luyanan
 * @since 2026/9/17
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoginController {


    private final LoginGrantDispatcher loginGrantDispatcher;

    @PostMapping("/login")
    public R login(HttpServletRequest request) {
        LoginUser loginUser = loginGrantDispatcher.authenticate(request);
        LoginHelper.login(loginUser); // 现有 Sa-Token
        String token = StpUtil.getTokenValue();
        long expiresIn = StpUtil.getTokenTimeout(); // 秒；按你们 sa-token 配置
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn > 0 ? expiresIn : null)
                .build();
        return R.ok(tokenResponse);
    }


    /**
     * 获取当前登陆人的信息
     *
     * @return
     * @since 2026/9/19
     */
    @GetMapping("/auth/me")
    public R getInfo() {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        if (null == loginUser) {
            throw new WarningException(401);
        }
        return R.ok(loginUser);
    }




}
