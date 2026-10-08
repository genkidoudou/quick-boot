package io.github.genkidoudou.web.service;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.api.SysMenuApi;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLoginGrantHandler implements LoginGrantHandler {


    @Autowired
    private SysMenuApi sysMenuApi;

    protected LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        List<SysMenuVo> sysMenuVos = sysMenuApi.listByRoleIds(new ArrayList<>());
//        loginUser.setClientId(oauthClientVo.getClientId());
//        List<String> roleKeys = sysPermissionService.listRoleKeys(user.getUserId() + "");
//        Set<String> permissions = sysPermissionService.listPermissions(user.getUserId() + "");
        loginUser.setPermissions(sysMenuVos.stream().map(SysMenuVo::getPerms).filter(StrUtil::isNotBlank).collect(Collectors.toSet()));
//        loginUser.setRolePermission(new HashSet<>(roleKeys));
        return loginUser;
    }
}
