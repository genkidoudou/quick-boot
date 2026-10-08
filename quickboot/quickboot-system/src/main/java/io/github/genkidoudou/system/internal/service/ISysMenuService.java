package io.github.genkidoudou.system.internal.service;

import cn.hutool.core.lang.tree.Tree;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.entity.SysMenu;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.internal.vo.SysMenuBatchRequestVo;

import java.util.List;

/**
 * 系统菜单服务。
 */
public interface ISysMenuService {


    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysMenuVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysMenu saveVo(SysMenuVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysMenuVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 根据角色id集合查询
     *
     * @param roleIds 角色id集合
     * @return
     * @since 2026/9/30
     */
    List<SysMenuVo> listByRoleIds(List<Long> roleIds);

    /**
     * 树形构建
     *
     * @param sysMenuVo 条件
     * @return
     * @since 2026/10/4
     */
    List<Tree<Long>> tree(SysMenuVo sysMenuVo);

    /**
     * 排序修改
     *
     * @param menuIds   菜单id
     * @param orderNums 排序
     * @since 2026/10/4
     */
    void sort(List<Long> menuIds, List<Integer> orderNums);

    /**
     * 一次保存菜单页及其按钮。
     *
     * @param menu    菜单
     * @param buttons 按钮，可空
     */
    void saveMenuWithButtons(SysMenuVo menu, List<SysMenuVo> buttons);

    /**
     * 从 Controller 源码解析菜单页与按钮。
     *
     * @param source 源码
     * @return 解析结果
     */
    SysMenuBatchRequestVo parseController(String source);
}
