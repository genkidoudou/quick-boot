package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.api.vo.SysRoleVo;

import java.util.List;

/**
 * 角色对外 API。
 */
public interface SysRoleApi {

    /**
     * 按角色主键集合查询自定义数据权限关联的部门 id。
     *
     * @param roleIds 角色主键集合
     * @return 部门 id 列表
     */
    List<Long> listDeptIdsByRoleIds(List<Long> roleIds);

    /**
     * 角色分页。
     *
     * @param request 分页与筛选
     * @return 分页结果
     */
    PageInfo<SysRoleVo> page(PageRequest<SysRoleVo> request);
}
