package io.github.genkidoudou.msg.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.msg.internal.service.IMsgGroupMemberService;
import io.github.genkidoudou.msg.internal.vo.MsgGroupMemberVo;
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
 * 群组成员管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("msg/groupMember")
public class MsgGroupMemberController {

    private final IMsgGroupMemberService msgGroupMemberService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("msg:groupMember:list")
    @PostMapping("page")
    public R<PageInfo<MsgGroupMemberVo>> page(@RequestBody PageRequest<MsgGroupMemberVo> pageRequest) {
        return R.ok(msgGroupMemberService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("msg:groupMember:query")
    @GetMapping("{id}")
    public R<MsgGroupMemberVo> get(@PathVariable Long id) {
        return R.ok(msgGroupMemberService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:groupMember:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) MsgGroupMemberVo body) {
        msgGroupMemberService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:groupMember:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) MsgGroupMemberVo body) {
        msgGroupMemberService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("msg:groupMember:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        msgGroupMemberService.removeByIds(ids);
        return R.ok();
    }
}
