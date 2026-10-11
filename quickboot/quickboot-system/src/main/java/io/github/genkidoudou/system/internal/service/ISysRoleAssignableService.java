package io.github.genkidoudou.system.internal.service;

import java.util.List;

/**
 * 可授权角色绑定服务（关联表，无 Vo）。
 */
public interface ISysRoleAssignableService {

    /**
     * 按角色主键查询可授权角色 id 列表。
     *
     * @param roleId 角色主键
     * @return 可授权角色 id 列表
     */
    List<Long> listAssignableRoleIdsByRoleId(Long roleId);

    /**
     * 覆盖角色与可授权角色的关联（先删后插；空列表表示清空）。
     *
     * @param roleId            角色主键
     * @param assignableRoleIds 可授权角色 id 列表
     */
    void replaceAssignableRoleIds(Long roleId, List<Long> assignableRoleIds);

    /**
     * 根据角色id集合 查询可授权角色 id 列表。
     *
     * @param roleIds 角色id集合
     * @return
     * @since 2026/10/11
     */
    List<Long> listAssignableRoleIdsByRoleIds(List<Long> roleIds);

}
