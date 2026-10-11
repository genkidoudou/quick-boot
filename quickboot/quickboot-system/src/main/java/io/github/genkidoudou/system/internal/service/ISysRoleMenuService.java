package io.github.genkidoudou.system.internal.service;

import java.util.List;

/**
 * 角色菜单绑定服务（关联表，无 Vo）。
 */
public interface ISysRoleMenuService {

    /**
     * 按角色主键查询菜单 id 列表。
     *
     * @param roleId 角色主键
     * @return 菜单 id 列表
     */
    List<Long> listMenuIdsByRoleId(Long roleId);

    /**
     * 覆盖角色与菜单的关联（先删后插；空列表表示清空）。
     *
     * @param roleId  角色主键
     * @param menuIds 菜单 id 列表
     */
    void replaceMenuIds(Long roleId, List<Long> menuIds);

    /**
     * 维护角色菜单关联
     *
     * @param roleId  角色id
     * @param menuIds 菜单id集合
     * @return
     * @since 2026/10/10
     */
    void saveOrUpdate(Long roleId, List<Long> menuIds);

    /**
     * 根据角色id集合删除
     *
     * @param roleIds 角色id集合
     * @return
     * @since 2026/10/10
     */
    void deleteByRoleIds(List<Long> roleIds);

    /**
     * 根据id集合查询关联的菜单id集合
     *
     * @param roleId 角色id
     * @return
     * @since 2026/10/10
     */
    List<Long> listByRoleId(Long roleId);

}
