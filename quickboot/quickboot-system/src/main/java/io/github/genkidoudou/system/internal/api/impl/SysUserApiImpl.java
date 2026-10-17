package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.service.ISysRoleService;
import io.github.genkidoudou.system.internal.service.ISysUserService;
import io.github.genkidoudou.system.api.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link SysUserApi} 实现。
 */
@Service
@RequiredArgsConstructor
public class SysUserApiImpl implements SysUserApi {

    private final ISysUserService sysUserService;

    private final ISysRoleService sysRoleService;

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

    @Override
    public SysUserVo getByUserId(Long userId) {
        return sysUserService.getVoById(userId);
    }

    @Override
    public List<String> listRoleCodesByUserId(Long userId) {
        List<SysRoleVo> sysRoleVoList = sysRoleService.listRoleByUserId(userId);
        return sysRoleVoList.stream().map(SysRoleVo::getRoleCode).distinct().collect(Collectors.toList());
    }

    @Override
    public List<SysRoleVo> listRoleListByUserId(Long userId) {
        return this.sysRoleService.listRoleByUserId(userId);
    }
}
