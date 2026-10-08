package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysMenuService;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.internal.vo.SysMenuBatchRequestVo;
import io.github.genkidoudou.system.internal.vo.SysMenuParseRequestVo;
import io.github.genkidoudou.system.internal.vo.SysMenuSortRequestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统菜单管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/menu")
public class SysMenuController {

    private final ISysMenuService sysMenuService;

    /**
     * 树形构建。
     *
     * @param sysMenuVo 查询条件
     * @return 结果
     */
    @SaCheckPermission("system:menu:list")
    @PostMapping("page")
    public R<List<Tree<Long>>> tree(@RequestBody SysMenuVo sysMenuVo) {
        return R.ok(sysMenuService.tree(sysMenuVo));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("{id}")
    public R<SysMenuVo> get(@PathVariable Long id) {
        return R.ok(sysMenuService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:menu:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysMenuVo body) {
        sysMenuService.saveVo(body);
        return R.ok();
    }

    /**
     * 一次保存菜单页及其按钮。
     *
     * @param body 菜单与按钮
     */
    @SaCheckPermission("system:menu:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("batch")
    public R<Void> batch(@RequestBody @Validated(AddGroup.class) SysMenuBatchRequestVo body) {
        sysMenuService.saveMenuWithButtons(body.getMenu(), body.getButtons());
        return R.ok();
    }

    /**
     * 从 Controller 源码解析菜单页与按钮。
     *
     * @param body 源码
     * @return 解析结果
     */
    @SaCheckPermission("system:menu:add")
    @PostMapping("parse")
    public R<SysMenuBatchRequestVo> parse(@RequestBody SysMenuParseRequestVo body) {
        return R.ok(sysMenuService.parseController(body.getSource()));
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:menu:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysMenuVo body) {
        sysMenuService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:menu:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysMenuService.removeByIds(ids);
        return R.ok();
    }

    /**
     * 排序
     *
     * @param sysMenuSortRequestVo 参数
     * @return
     * @since 2026/10/4
     */
    @SaCheckPermission("system:menu:edit")
    @PostMapping("sort")
    public R sort(@RequestBody SysMenuSortRequestVo sysMenuSortRequestVo) {

        sysMenuService.sort(sysMenuSortRequestVo.getMenuIds(), sysMenuSortRequestVo.getOrderNums());
        return R.ok();
    }
}
