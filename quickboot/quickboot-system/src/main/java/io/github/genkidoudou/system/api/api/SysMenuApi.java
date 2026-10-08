package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.system.api.vo.SysMenuVo;

import java.util.List;

public interface SysMenuApi {

    /**
     * 根据角色id集合查询关联的菜单id
     *
     * @param roleIds 角色id集合
     * @return
     * @since 2026/9/30
     */
    List<SysMenuVo> listByRoleIds(List<Long> roleIds);

}
