package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.datascope.annotation.DataPermission;
import io.github.genkidoudou.common.desensitization.SensitiveResponse;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.service.ISysUserService;
import io.github.genkidoudou.system.internal.vo.UpdatePasswordRequestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @SensitiveResponse
    @SaCheckPermission("system:user:list")
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
    @SaCheckPermission("system:user:query")
    @GetMapping("{id}")
    public R<SysUserVo> get(@PathVariable Long id) {
        return R.ok(sysUserService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:user:add")
    @Idempotent(ttlSeconds = -1, key = "#userId")
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
    @SaCheckPermission("system:user:edit")
    @Idempotent(ttlSeconds = -1, key = "#userId")
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
    @SaCheckPermission("system:user:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysUserService.removeByIds(ids);
        return R.ok();
    }


    /**
     * 重置密码
     *
     * @param userId 用户id
     * @return
     * @since 2026/10/13
     */
    @PostMapping("resetPassword/{userId}")
    public R resetPassword(@PathVariable("userId") Long userId) {
        sysUserService.resetPassword(userId);
        return R.ok();
    }

    /**
     * 提供密码进行二次认证
     *
     * @param password 密码
     * @return
     * @since 2026/10/13
     */
    @PostMapping("openSafe")
    public R openSafe(@RequestParam(value = "password", required = true) String password) {
        sysUserService.checkPassword(LoginUserUtils.getLoginUser().getUserId(), password);
        return R.ok();
    }


    /**
     * 修改当前用户的密码
     *
     * @param updatePasswordRequestVo 参数
     * @return
     * @since 2026/10/13
     */
    @PostMapping("updatePassword")
    public R updatePassword(@RequestBody @Validated UpdatePasswordRequestVo updatePasswordRequestVo) {
        sysUserService.updatePassword(LoginUserUtils.getLoginUser().getUserId(), updatePasswordRequestVo);
        return R.ok();
    }

    /**
     * 当前登录用户个人资料。
     */
    @GetMapping("profile")
    public R<SysUserVo> profile() {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        return R.ok(sysUserService.getProfile(loginUser.getUserId()));
    }

    /**
     * 更新当前登录用户个人资料（昵称、邮箱、手机、性别）。
     */
    @Idempotent(ttlSeconds = -1, key = "#userId")
    @PostMapping("updateProfile")
    public R<Void> updateProfile(@RequestBody SysUserVo body) {
        LoginUser loginUser = LoginUserUtils.getLoginUser();
        sysUserService.updateProfile(loginUser.getUserId(), body);
        return R.ok();
    }


}
