package io.github.genkidoudou.job.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.job.internal.service.ISysJobLogService;
import io.github.genkidoudou.job.internal.vo.SysJobLogVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 定时任务日志接口。只提供查询和删除。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("job/jobLog")
public class SysJobLogController {

    private final ISysJobLogService sysJobLogService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("job:jobLog:list")
    @PostMapping("page")
    public R<PageInfo<SysJobLogVo>> page(@RequestBody PageRequest<SysJobLogVo> pageRequest) {
        return R.ok(sysJobLogService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("job:jobLog:query")
    @GetMapping("{id}")
    public R<SysJobLogVo> get(@PathVariable Long id) {
        return R.ok(sysJobLogService.getVoById(id));
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("job:jobLog:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysJobLogService.removeByIds(ids);
        return R.ok();
    }
}
