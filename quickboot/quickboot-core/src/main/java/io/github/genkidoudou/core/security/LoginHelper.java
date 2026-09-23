package io.github.genkidoudou.core.security;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import io.github.genkidoudou.common.security.vo.LoginUser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Sa-Token 登录辅助：封装登录、按 OAuth 客户端设备维度登录及 {@link LoginUser} 会话缓存。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    /**
     * Token Session 中存储 {@link LoginUser} 的键名。
     */
    public static final String LOGIN_USER_KEY = "loginUser";

    /**
     * 登录系统
     *
     * @param loginUser 登录用户信息
     */
    public static void login(LoginUser loginUser) {
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        StpUtil.login(loginUser.getUserId());
        setLoginUser(loginUser);
    }


    public static LoginUser getLogin() {

        Object object = SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (null == object) {
            object = StpUtil.getTokenSession().get(LOGIN_USER_KEY);
        }
        if (null == object) {
            return null;
        } else {
            return (LoginUser) object;
        }
    }


    /**
     * 设置用户数据（写入 Token Session 多级缓存）。
     *
     * @param loginUser 登录用户信息
     */
    public static void setLoginUser(LoginUser loginUser) {
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }
}
