package io.github.genkidoudou.system.api.api;

import java.util.List;

/**
 * 角色对外 API。
 */
public interface SysRoleApi {

    /**
     * 按角色主键集合查询自定义数据权限关联的部门 id。
     *
     * @param roleIds 角色主键集合
     * @return 部门 id 列表
     */
    List<Long> listDeptIdsByRoleIds(List<Long> roleIds);
}
