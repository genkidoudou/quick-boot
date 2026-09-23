package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.entity.SysUser;

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
}
