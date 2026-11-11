package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.api.api.SysRoleApi;
import io.github.genkidoudou.system.api.vo.SysRoleVo;
import io.github.genkidoudou.system.internal.service.ISysRoleDeptService;
import io.github.genkidoudou.system.internal.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link SysRoleApi} 实现。
 */
@Service
@RequiredArgsConstructor
public class SysRoleApiImpl implements SysRoleApi {

    private final ISysRoleDeptService sysRoleDeptService;

    private final ISysRoleService sysRoleService;

    /**
     * 按角色主键集合查询自定义数据权限关联的部门 id。
     *
     * @param roleIds 角色主键集合
     * @return 部门 id 列表
     */
    @Override
    public List<Long> listDeptIdsByRoleIds(List<Long> roleIds) {
        return sysRoleDeptService.listDeptIdsByRoleIds(roleIds);
    }

    @Override
    public PageInfo<SysRoleVo> page(PageRequest<SysRoleVo> request) {
        return sysRoleService.pageVo(request);
    }
}
