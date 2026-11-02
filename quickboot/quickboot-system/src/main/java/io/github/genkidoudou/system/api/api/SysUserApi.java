package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.api.vo.SysRoleVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;

import java.util.List;

/**
 * 系统用户对外 API。
 */
public interface SysUserApi {

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 用户视图，不存在时由实现返回 {@code null}
     * @since 2026/9/18
     */
    SysUserVo findByUserName(String username);

    /**
     * 根据用户id查询
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/13
     */
    SysUserVo getByUserId(Long userId);

    /**
     * 根据用户id查询角色编码
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/16
     */
    List<String> listRoleCodesByUserId(Long userId);


    /**
     * 根据用户id查询关联的角色id集合
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/17
     */
    List<SysRoleVo> listRoleListByUserId(Long userId);

    /**
     * 根据用户id集合查询
     *
     * @param userIds id集合
     * @return
     * @since 2026/11/1
     */
    List<SysUserVo> listByUserIds(List<Long> userIds);

    /**
     * 分页查询
     *
     * @param request 分页参数
     * @param userIds 排除的用户id集合
     * @return
     * @since 2026/11/1
     */
    PageInfo<SysUserVo> page(PageRequest<SysUserVo> request, List<Long> userIds);
}
