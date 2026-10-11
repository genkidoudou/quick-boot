package io.github.genkidoudou.system.internal.service;

import java.util.List;

/**
 * 用户角色绑定服务（关联表，无 Vo）。
 */
public interface ISysUserRoleService {

    /**
     * 按用户主键查询角色 id 列表。
     *
     * @param userId 用户主键
     * @return 角色 id 列表
     */
    List<Long> listRoleIdsByUserId(Long userId);

    /**
     * 覆盖用户与角色的关联（先删后插；空列表表示清空）。
     *
     * @param userId  用户主键
     * @param roleIds 角色 id 列表
     */
    void replaceRoleIds(Long userId, List<Long> roleIds);
}
