package io.github.genkidoudou.system.internal.service;

import cn.hutool.core.lang.tree.Tree;
import io.github.genkidoudou.system.internal.entity.SysDept;
import io.github.genkidoudou.system.internal.vo.SysDeptVo;

import java.util.List;

/**
 * 部门服务。
 */
public interface ISysDeptService {

    /**
     * 按条件查询并组装为树（根 parentId=0）。
     *
     * @param sysDeptVo 查询条件
     * @return 树形部门
     */
    List<Tree<Long>> tree(SysDeptVo sysDeptVo);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysDeptVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysDept saveVo(SysDeptVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysDeptVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 根据部门id集合查询
     *
     * @param deptIds id集合
     * @return
     * @since 2026/10/12
     */
    List<SysDeptVo> listByIds(List<Long> deptIds);
}
