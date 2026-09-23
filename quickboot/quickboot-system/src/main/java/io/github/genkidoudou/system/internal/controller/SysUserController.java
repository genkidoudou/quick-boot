package io.github.genkidoudou.system.internal.controller;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.service.ISysUserService;
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
 * 系统用户管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/user")
public class SysUserController {

    private final ISysUserService sysUserService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @PostMapping("page")
    public R<PageInfo<SysUserVo>> page(@RequestBody PageRequest<SysUserVo> pageRequest) {
        return R.ok(sysUserService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("{id}")
    public R<SysUserVo> get(@PathVariable Long id) {
        return R.ok(sysUserService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysUserVo body) {
        sysUserService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。密码为空时不覆盖原密码。
     *
     * @param body 表单数据
     */
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysUserVo body) {
        sysUserService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysUserService.removeByIds(ids);
        return R.ok();
    }
}
