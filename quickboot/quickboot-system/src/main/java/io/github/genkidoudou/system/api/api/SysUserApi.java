package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.system.api.vo.SysUserVo;

/**
 * 系统用户对外 API。
 */
public interface SysUserApi {

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 用户视图，不存在时由实现返回 {@code null}
     * @since 2026/9/18
     */
    SysUserVo findByUserName(String username);
}
