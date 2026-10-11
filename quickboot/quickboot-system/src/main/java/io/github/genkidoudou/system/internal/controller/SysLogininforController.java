package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.system.internal.service.ISysLogininforService;
import io.github.genkidoudou.system.internal.vo.SysLogininforVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志接口。仅分页与详情，不提供新增、修改、删除。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/logininfor")
public class SysLogininforController {

    private final ISysLogininforService sysLogininforService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:logininfor:list")
    @PostMapping("page")
    public R<PageInfo<SysLogininforVo>> page(@RequestBody PageRequest<SysLogininforVo> pageRequest) {
        return R.ok(sysLogininforService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:logininfor:query")
    @GetMapping("{id}")
    public R<SysLogininforVo> get(@PathVariable Long id) {
        return R.ok(sysLogininforService.getVoById(id));
    }
}
