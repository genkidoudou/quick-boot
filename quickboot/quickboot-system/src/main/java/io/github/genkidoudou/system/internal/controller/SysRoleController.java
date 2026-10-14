package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysRoleService;
import io.github.genkidoudou.system.internal.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/role")
public class SysRoleController {

    private final ISysRoleService sysRoleService;
    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:role:list")
    @PostMapping("page")
    public R<PageInfo<SysRoleVo>> page(@RequestBody PageRequest<SysRoleVo> pageRequest) {
        return R.ok(sysRoleService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("{id}")
    public R<SysRoleVo> get(@PathVariable Long id) {
        return R.ok(sysRoleService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:role:add")
    @Idempotent(ttlSeconds = -1, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysRoleVo body) {
        sysRoleService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:role:edit")
    @Idempotent(ttlSeconds = -1, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysRoleVo body) {
        sysRoleService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:role:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysRoleService.removeByIds(ids);
        return R.ok();
    }


    /**
     * 获取当前用户可授权的角色列表
     *
     * @return
     * @since 2026/10/11
     */
    @GetMapping("assignable")
    public R<List<SysRoleVo>> assignable() {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        List<SysRoleVo> sysRoleVoList = sysRoleService.getAssignableByUserId(loginUser.getUserId());
        return R.ok(sysRoleVoList);
    }


}
