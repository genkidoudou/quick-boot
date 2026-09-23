package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * {@link SysUserApi} 实现。
 */
@Service
@RequiredArgsConstructor
public class SysUserApiImpl implements SysUserApi {

    private final ISysUserService sysUserService;

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 用户视图，不存在时返回 {@code null}
     */
    @Override
    public SysUserVo findByUserName(String username) {
        return sysUserService.findByUserName(username);
    }
}
