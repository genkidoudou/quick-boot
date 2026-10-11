package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysDeptService;
import io.github.genkidoudou.system.internal.vo.SysDeptVo;
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
 * 部门管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/dept")
public class SysDeptController {

    private final ISysDeptService sysDeptService;

    /**
     * 树形列表（与菜单一致：POST page 返回树，非分页）。
     *
     * @param sysDeptVo 查询条件
     * @return 树形结果
     */
    @SaCheckPermission("system:dept:list")
    @PostMapping("page")
    public R<List<Tree<Long>>> tree(@RequestBody(required = false) SysDeptVo sysDeptVo) {
        return R.ok(sysDeptService.tree(sysDeptVo));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping("{id}")
    public R<SysDeptVo> get(@PathVariable Long id) {
        return R.ok(sysDeptService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:dept:add")
    @Idempotent(ttlSeconds = -1, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysDeptVo body) {
        sysDeptService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:dept:edit")
    @Idempotent(ttlSeconds = -1, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysDeptVo body) {
        sysDeptService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:dept:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysDeptService.removeByIds(ids);
        return R.ok();
    }
}
