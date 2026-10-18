package io.github.genkidoudou.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.api.TraceIds;
import io.github.genkidoudou.common.crypto.PasswordCodec;
import io.github.genkidoudou.common.crypto.PasswordCodecFactories;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.file.QcFileProperties;
import io.github.genkidoudou.common.file.url.FileUrlSupport;
import io.github.genkidoudou.common.monitor.operlog.IgnoreLogger;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.common.utils.ServletUtils;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.core.security.LoginHelper;
import io.github.genkidoudou.system.api.api.SysConfigApi;
import io.github.genkidoudou.system.api.api.SysMenuApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.RouterVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.web.event.UserLoginEvent;
import io.github.genkidoudou.web.event.UserLoginEventVo;
import io.github.genkidoudou.web.service.LoginGrantDispatcher;
import io.github.genkidoudou.web.vo.TokenResponse;
import io.github.genkidoudou.web.vo.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 登录
 *
 * @author luyanan
 * @since 2026/9/17
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@IgnoreLogger
public class LoginController {

    /**
     * SYS_LOGIN_STATUS：成功
     */
    private static final String LOGIN_STATUS_SUCCESS = "0";

    /**
     * SYS_LOGIN_STATUS：失败
     */
    private static final String LOGIN_STATUS_FAIL = "1";

    private final LoginGrantDispatcher loginGrantDispatcher;

    private final SysMenuApi sysMenuApi;

    private final SysUserApi sysUserApi;

    private final SysConfigApi sysConfigApi;

    private final QcFileProperties qcFileProperties;

    @PostMapping("/login")
    public R login(HttpServletRequest request) {
        LoginUser loginUser = null;
        TokenResponse tokenResponse = null;
        String loginStatus = LOGIN_STATUS_SUCCESS;
        try {
            loginUser = loginGrantDispatcher.authenticate(request);
            LoginHelper.login(loginUser);
            String token = StpUtil.getTokenValue();
            long expiresIn = StpUtil.getTokenTimeout();
            tokenResponse = TokenResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn > 0 ? expiresIn : null)
                    .build();
            return R.ok(tokenResponse);
        } catch (RuntimeException e) {
            loginStatus = LOGIN_STATUS_FAIL;
            throw e;
        } catch (Exception e) {
            loginStatus = LOGIN_STATUS_FAIL;
            throw new RuntimeException(e);
        } finally {
            publishLoginEvent(request, loginUser, loginStatus);
        }
    }

    /**
     * 发布登录日志事件；失败不影响登录主流程。
     * <p>
     * 失败时 {@code loginUser} 可能为空：用户名取请求参数，userId 由监听器按用户名补查。
     * </p>
     */
    private void publishLoginEvent(HttpServletRequest request, LoginUser loginUser, String loginStatus) {
        try {
            String userName = loginUser != null && StrUtil.isNotBlank(loginUser.getUsername())
                    ? loginUser.getUsername()
                    : request.getParameter("username");
            String clientId = loginUser != null && StrUtil.isNotBlank(loginUser.getClientId())
                    ? loginUser.getClientId()
                    : StrUtil.blankToDefault(request.getParameter("clientId"), "");

            String clientIP = ServletUtils.getClientIP(request);
            String userAgent = ServletUtils.getHeader(request, "User-Agent", StandardCharsets.UTF_8);
            UserAgent agent = UserAgentUtil.parse(userAgent);
            String browser = agent != null && agent.getBrowser() != null ? agent.getBrowser().getName() : "";
            String os = agent != null && agent.getOs() != null ? agent.getOs().getName() : "";

            UserLoginEventVo loginEventVo = UserLoginEventVo.builder()
                    .userId(loginUser != null ? loginUser.getUserId() : null)
                    .userName(userName)
                    .clientId(clientId)
                    .ipAddr(clientIP)
                    .browser(browser)
                    .os(os)
                    .status(loginStatus)
                    .loginTime(LocalDateTime.now())
                    .traceId(TraceIds.currentOrCreate())
                    .build();
            SpringUtil.publishEvent(new UserLoginEvent(loginEventVo));
        } catch (Exception e) {
            log.warn("publish UserLoginEvent failed: {}", e.getMessage());
        }
    }

    /**
     * 获取当前登陆人的信息
     *
     * @return 当前用户信息
     * @since 2026/9/19
     */
    @GetMapping("/auth/me")
    public R<UserInfoVo> getInfo() {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        if (null == loginUser) {
            throw new WarningException(401);
        }

        SysUserVo sysUserVo = sysUserApi.getByUserId(loginUser.getUserId());
        if (null == sysUserVo) {
            throw new WarningException(10006);
        }

        // 1. 是否为默认密码
        String defaultPassword = sysConfigApi.getConfigValue("default_password", true);
        PasswordCodec passwordCodec = PasswordCodecFactories.get(CommonEnums.PASSWORD_CODEC_ENCODED.getValue());
        boolean isDefaultPassword = passwordCodec.matches(defaultPassword, sysUserVo.getPassword());
        UserInfoVo infoVo = UserInfoVo.builder()
                .userId(sysUserVo.getId())
                .deptId(sysUserVo.getDeptId())
                .permissions(loginUser.getPermissions())
                .username(sysUserVo.getUserName())
                .nickName(sysUserVo.getNickName())
                .avatar(FileUrlSupport.resolvePublicUrl(qcFileProperties, sysUserVo.getAvatar()))
                .defaultPassword(isDefaultPassword)
                .passwordExpired(sysUserVo.getPasswordExpireTime() != null
                        && DateUtil.compare(sysUserVo.getPasswordExpireTime(), new Date()) < 0)
                .build();
        return R.ok(infoVo);
    }

    /**
     * 获取当前登陆人的菜单路由
     *
     * @return 路由树
     * @since 2026/10/8
     */
    @GetMapping("sys/menu/routes")
    public R<List<RouterVo>> routes() {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        return R.ok(sysMenuApi.selectMenuTreeByUserId(loginUser.getUserId()));
    }
}
