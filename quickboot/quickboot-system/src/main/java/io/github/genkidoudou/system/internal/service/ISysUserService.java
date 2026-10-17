package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.entity.SysUser;
import io.github.genkidoudou.system.internal.vo.UpdatePasswordRequestVo;

import java.util.List;

/**
 * 系统用户服务。
 */
public interface ISysUserService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysUserVo> pageVo(PageRequest<SysUserVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysUserVo getVoById(Long id);

    /**
     * 新增用户（含密码加密）。
     *
     * @param sysUserVo 用户视图
     * @return 持久化后的实体
     */
    SysUser saveVo(SysUserVo sysUserVo);

    /**
     * 修改用户；密码为空则不更新密码字段。
     *
     * @param sysUserVo 用户视图
     * @return 是否成功
     */
    boolean updateVoById(SysUserVo sysUserVo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 根据用户名查询用户视图（含密码，供登录校验）。
     *
     * @param username 用户名
     * @return 用户视图，不存在时返回 {@code null}
     */
    SysUserVo findByUserName(String username);

    /**
     * 重置密码
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/13
     */
    void resetPassword(Long userId);

    /**
     * 检查用户的密码是否正确
     *
     * @param userId   用户id
     * @param password 密码
     * @return
     * @since 2026/10/13
     */
    void checkPassword(Long userId, String password);

    /**
     * 根据id 修改密码
     *
     * @param userId                  用户id
     * @param updatePasswordRequestVo 密码
     * @return
     * @since 2026/10/13
     */
    void updatePassword(Long userId, UpdatePasswordRequestVo updatePasswordRequestVo);

    /**
     * 当前登录用户个人资料（含角色名、部门名）。
     *
     * @param userId 用户 id
     * @return 个人资料
     */
    SysUserVo getProfile(Long userId);

    /**
     * 更新当前登录用户个人资料（昵称、邮箱、手机、性别）。
     *
     * @param userId 用户 id
     * @param body   表单
     */
    void updateProfile(Long userId, SysUserVo body);

}
