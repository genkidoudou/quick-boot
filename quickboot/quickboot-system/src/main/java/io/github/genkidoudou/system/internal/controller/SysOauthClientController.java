package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.entity.SysOauthClient;
import io.github.genkidoudou.system.internal.service.ISysOauthClientService;
import io.github.genkidoudou.system.internal.vo.SysOauthClientVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OAuth 客户端管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/oauthclient")
public class SysOauthClientController {

    private final ISysOauthClientService sysOauthClientService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:oauthClient:list")
    @PostMapping("page")
    public R<PageInfo<SysOauthClientVo>> page(@RequestBody PageRequest<SysOauthClientVo> pageRequest) {
        return R.ok(sysOauthClientService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:oauthClient:query")
    @GetMapping("{id}")
    public R<SysOauthClientVo> get(@PathVariable Long id) {
        return R.ok(sysOauthClientService.getVoById(id));
    }

    /**
     * 新增。密钥由服务端生成，响应中一次性返回明文 {@code clientSecret}。
     *
     * @param body 表单数据
     * @return 新建结果（含明文密钥）
     */
    @SaCheckPermission("system:oauthClient:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<SysOauthClientVo> add(@RequestBody @Validated(AddGroup.class) SysOauthClientVo body) {
        SysOauthClient entity = sysOauthClientService.saveVo(body);
        SysOauthClientVo created = new SysOauthClientVo();
        BeanUtils.copyProperties(entity, created);
        return R.ok(created);
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:oauthClient:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysOauthClientVo body) {
        sysOauthClientService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:oauthClient:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysOauthClientService.removeByIds(ids);
        return R.ok();
    }
}
