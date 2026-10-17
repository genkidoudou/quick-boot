package io.github.genkidoudou.web.service;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.api.SysMenuApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractLoginGrantHandler implements LoginGrantHandler {

    private final SysMenuApi sysMenuApi;

    private final SysUserApi sysUserApi;

    protected LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        List<String> perms = sysMenuApi.listPermsByUserId(user.getId());
        loginUser.setPermissions(new HashSet<>(perms));
        List<String> roleCodes = sysUserApi.listRoleCodesByUserId(user.getId());
        loginUser.setRoles(new HashSet<>(roleCodes));
        return loginUser;
    }
}
