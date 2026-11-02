package io.github.genkidoudou.msg.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.msg.internal.service.IMsgInboxService;
import io.github.genkidoudou.msg.internal.vo.MsgInboxVo;
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
 * 站内信收件箱管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("msg/inbox")
public class MsgInboxController {

    private final IMsgInboxService msgInboxService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("msg:inbox:list")
    @PostMapping("page")
    public R<PageInfo<MsgInboxVo>> page(@RequestBody PageRequest<MsgInboxVo> pageRequest) {
        return R.ok(msgInboxService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("msg:inbox:query")
    @GetMapping("{id}")
    public R<MsgInboxVo> get(@PathVariable Long id) {
        return R.ok(msgInboxService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:inbox:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) MsgInboxVo body) {
        msgInboxService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:inbox:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) MsgInboxVo body) {
        msgInboxService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("msg:inbox:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        msgInboxService.removeByIds(ids);
        return R.ok();
    }
}
