package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.system.api.vo.RouterVo;
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


    /**
     * 根据用户id获取前端动态路由
     *
     * @param userId 用户id
     * @return 路由列表
     * @since 2026/10/8
     */
    List<RouterVo> selectMenuTreeByUserId(Long userId);

    /**
     * 当前用户的权限标识。超级管理员返回 {@code *:*:*}。
     *
     * @param userId 用户 id
     * @return 权限标识
     */
    List<String> listPermsByUserId(Long userId);

    /**
     * 当前用户已启用角色的角色编码。
     *
     * @param userId 用户 id
     * @return 角色编码
     */
    List<String> listRoleCodesByUserId(Long userId);
}
