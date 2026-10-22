package io.github.genkidoudou.job.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
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
 * 定时任务管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("job/job")
public class SysJobController {

    private final ISysJobService sysJobService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("job:job:list")
    @PostMapping("page")
    public R<PageInfo<SysJobVo>> page(@RequestBody PageRequest<SysJobVo> pageRequest) {
        return R.ok(sysJobService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("job:job:query")
    @GetMapping("{id}")
    public R<SysJobVo> get(@PathVariable Long id) {
        return R.ok(sysJobService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("job:job:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysJobVo body) {
        sysJobService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("job:job:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysJobVo body) {
        sysJobService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("job:job:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysJobService.removeByIds(ids);
        return R.ok();
    }

    /**
     * 修改状态
     *
     * @param body
     * @return
     * @since 2026/10/22
     */
    @SaCheckPermission("job:job:edit")
    @PostMapping("changeStatus")
    public R<Void> changeStatus(@RequestBody SysJobVo body) {
        sysJobService.changeStatus(body.getId(), body.getStatus());
        return R.ok();
    }

    /**
     * 执行一次
     *
     * @param id 任务id
     * @return
     * @since 2026/10/22
     */
    @SaCheckPermission("job:job:edit")
    @PostMapping("run/{id}")
    public R<Void> run(@PathVariable Long id) {
        sysJobService.run(id);
        return R.ok();
    }
}
