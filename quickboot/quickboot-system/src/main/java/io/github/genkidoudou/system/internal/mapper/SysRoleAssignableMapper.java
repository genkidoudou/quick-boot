package io.github.genkidoudou.system.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.system.internal.entity.SysRoleAssignable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 可授权角色关联 Mapper。
 */
@Mapper
public interface SysRoleAssignableMapper extends BaseBaseMapper<SysRoleAssignable> {
}
