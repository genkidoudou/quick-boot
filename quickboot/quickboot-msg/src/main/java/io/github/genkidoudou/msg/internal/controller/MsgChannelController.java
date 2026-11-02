package io.github.genkidoudou.msg.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.msg.internal.service.IMsgChannelService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
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
 * 消息渠道管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("msg/channel")
public class MsgChannelController {

    private final IMsgChannelService msgChannelService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("msg:channel:list")
    @PostMapping("page")
    public R<PageInfo<MsgChannelVo>> page(@RequestBody PageRequest<MsgChannelVo> pageRequest) {
        return R.ok(msgChannelService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("msg:channel:query")
    @GetMapping("{id}")
    public R<MsgChannelVo> get(@PathVariable Long id) {
        return R.ok(msgChannelService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:channel:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) MsgChannelVo body) {
        msgChannelService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:channel:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) MsgChannelVo body) {
        msgChannelService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("msg:channel:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        msgChannelService.removeByIds(ids);
        return R.ok();
    }
}
