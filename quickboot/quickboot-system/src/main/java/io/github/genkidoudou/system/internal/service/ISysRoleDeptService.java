package io.github.genkidoudou.system.internal.service;

import java.util.List;

/**
 * 角色部门绑定服务（关联表，无 Vo）。
 */
public interface ISysRoleDeptService {

    /**
     * 按角色主键查询部门 id 列表。
     *
     * @param roleId 角色主键
     * @return 部门 id 列表
     */
    List<Long> listDeptIdsByRoleId(Long roleId);

    /**
     * 按角色主键集合查询部门 id 列表。
     *
     * @param roleIds 角色主键集合
     * @return 部门 id 列表
     */
    List<Long> listDeptIdsByRoleIds(List<Long> roleIds);

    /**
     * 覆盖角色与部门的关联（先删后插；空列表表示清空）。
     *
     * @param roleId  角色主键
     * @param deptIds 部门 id 列表
     */
    void replaceDeptIds(Long roleId, List<Long> deptIds);

    /**
     * 按角色主键集合删除关联。
     *
     * @param roleIds 角色主键集合
     */
    void deleteByRoleIds(List<Long> roleIds);
}
