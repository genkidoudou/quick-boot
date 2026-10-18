package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysFileClassifyService;
import io.github.genkidoudou.system.internal.vo.SysFileClassifyVo;
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
 * 文件分类管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/fileClassify")
public class SysFileClassifyController {

    private final ISysFileClassifyService sysFileClassifyService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:fileClassify:list")
    @PostMapping("page")
    public R<PageInfo<SysFileClassifyVo>> page(@RequestBody PageRequest<SysFileClassifyVo> pageRequest) {
        return R.ok(sysFileClassifyService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:fileClassify:query")
    @GetMapping("{id}")
    public R<SysFileClassifyVo> get(@PathVariable Long id) {
        return R.ok(sysFileClassifyService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:fileClassify:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysFileClassifyVo body) {
        sysFileClassifyService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:fileClassify:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysFileClassifyVo body) {
        sysFileClassifyService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:fileClassify:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysFileClassifyService.removeByIds(ids);
        return R.ok();
    }
}
