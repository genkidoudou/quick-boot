package io.github.genkidoudou.web.support;

import io.github.genkidoudou.common.datascope.DataScopeContext;
import io.github.genkidoudou.common.datascope.DataScopeSupport;
import io.github.genkidoudou.common.datascope.annotation.DataPermission;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.api.SysDeptApi;
import io.github.genkidoudou.system.api.api.SysRoleApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.enums.UserDataScopeEnums;
import io.github.genkidoudou.system.api.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DataScopeSupportImpl implements DataScopeSupport {


    private final SysUserApi sysUserApi;

    private final SysDeptApi sysDeptApi;

    private final SysRoleApi sysRoleApi;

    @Override
    public DataScopeContext.DataScope getScope(LoginUser loginUser, DataPermission dataPermission) {
        // 当不登陆的时候就查询全部
        if (null == loginUser) {
            return new DataScopeContext.DataScope(true, new HashSet<>(), true, null, dataPermission);
        }

        Long userId = loginUser.getUserId();
        List<SysRoleVo> sysRoleVoList = sysUserApi.listRoleListByUserId(userId);
        if (sysRoleVoList.stream().anyMatch(role -> UserDataScopeEnums.of(role.getDataScope()) == UserDataScopeEnums.ALL)) {
            // 全局角色
            return new DataScopeContext.DataScope(true, new HashSet<>(), true, userId, dataPermission);
        }


        Set<Long> deptIds = new HashSet<>();
        List<Long> customRoleIds = new ArrayList<>();
        boolean includeSelf = false;
        for (SysRoleVo role : sysRoleVoList) {
            UserDataScopeEnums scope = UserDataScopeEnums.of(role.getDataScope());
            if (scope == null) {
                continue;
            }
            switch (scope) {
                // 本部门的权限
                case DEPT -> {
                    if (loginUser.getDeptId() != null) {
                        deptIds.add(loginUser.getDeptId());
                    }
                }
                // 本部门以及以下
                case DEPT_AND_CHILD -> deptIds.addAll(sysDeptApi.listSelfAndChildIds(loginUser.getDeptId()));
                // 自定义
                case CUSTOM -> {
                    if (role.getId() != null) {
                        customRoleIds.add(role.getId());
                    }
                }
                //自己
                case SELF -> includeSelf = true;
                case ALL -> {
                }
            }
        }
        if (!customRoleIds.isEmpty()) {
            deptIds.addAll(sysRoleApi.listDeptIdsByRoleIds(customRoleIds));
        }
        deptIds.remove(null);
        return new DataScopeContext.DataScope(false, deptIds, includeSelf, loginUser.getUserId(), dataPermission);
    }
}
