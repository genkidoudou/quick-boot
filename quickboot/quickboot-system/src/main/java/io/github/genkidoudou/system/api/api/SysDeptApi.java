package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.system.api.vo.SysDeptOptionVo;

import java.util.List;
import java.util.Map;

/**
 * 部门api
 *
 * @author luyanan
 * @since 2026/10/17
 */
public interface SysDeptApi {


    /**
     * 查询自己和他的所有子类
     *
     * @param deptId 部门id
     * @return
     * @since 2026/10/17
     */
    List<Long> listSelfAndChildIds(Long deptId);


    /**
     * 根据角色id 获取自定义的部门id集合
     *
     * @param roleIds 角色id集合
     * @return
     * @since 2026/10/17
     */
    List<Long> listByRoleId(List<Long> roleIds);

    /**
     * 全部部门，按排序返回，供组装部门树。
     *
     * @return 部门简要信息
     */
    List<SysDeptOptionVo> listAll();
}
