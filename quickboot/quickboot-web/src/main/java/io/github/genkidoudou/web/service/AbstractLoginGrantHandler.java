package io.github.genkidoudou.web.service;

import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.vo.SysUserVo;

public abstract class AbstractLoginGrantHandler implements LoginGrantHandler {


    protected LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
//        loginUser.setClientId(oauthClientVo.getClientId());
//        List<String> roleKeys = sysPermissionService.listRoleKeys(user.getUserId() + "");
//        Set<String> permissions = sysPermissionService.listPermissions(user.getUserId() + "");
//        loginUser.setMenuPermission(permissions);
//        loginUser.setRolePermission(new HashSet<>(roleKeys));
        return loginUser;
    }
}
