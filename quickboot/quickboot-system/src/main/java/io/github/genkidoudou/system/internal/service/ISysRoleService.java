package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.entity.SysRole;
import io.github.genkidoudou.system.internal.vo.SysRoleVo;

import java.util.List;

/**
 * 角色服务。
 */
public interface ISysRoleService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysRoleVo> pageVo(PageRequest<SysRoleVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysRoleVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysRole saveVo(SysRoleVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysRoleVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);


    /**
     * 根据用户id获取可授权的角色集合
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/11
     */
    List<SysRoleVo> getAssignableByUserId(Long userId);

    /**
     * 根据用户id查询关联的角色id集合
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/11
     */
    List<Long> listRoleByUserId(Long userId);

    /**
     * 绑定用户和角色
     *
     * @param userId  用户id
     * @param roleIds 角色id集合
     * @return
     * @since 2026/10/11
     */
    void bindUserRole(Long userId, List<Long> roleIds);

    /**
     * 解除绑定关系
     *
     * @param userIds 用户id集合
     * @return
     * @since 2026/10/11
     */
    void deleteBindRole(List<Long> userIds);
}
