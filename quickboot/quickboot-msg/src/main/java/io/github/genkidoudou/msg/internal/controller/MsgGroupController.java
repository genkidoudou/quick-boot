package io.github.genkidoudou.msg.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.msg.internal.service.IMsgGroupService;
import io.github.genkidoudou.msg.internal.vo.MsgGroupDeptNode;
import io.github.genkidoudou.msg.internal.vo.MsgGroupMemberVo;
import io.github.genkidoudou.msg.internal.vo.MsgGroupVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
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
 * 消息群组管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("msg/group")
public class MsgGroupController {

    private final IMsgGroupService msgGroupService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("msg:group:list")
    @PostMapping("page")
    public R<PageInfo<MsgGroupVo>> page(@RequestBody PageRequest<MsgGroupVo> pageRequest) {
        return R.ok(msgGroupService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("msg:group:query")
    @GetMapping("{id}")
    public R<MsgGroupVo> get(@PathVariable Long id) {
        return R.ok(msgGroupService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:group:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Long> add(@RequestBody @Validated(AddGroup.class) MsgGroupVo body) {
        return R.ok(msgGroupService.saveVo(body).getId());
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("msg:group:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) MsgGroupVo body) {
        msgGroupService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("msg:group:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        msgGroupService.removeByIds(ids);
        return R.ok();
    }


    // 已经选择的用户

    /**
     * 已经选择的用户
     *
     * @param groupId 分组id
     * @return
     * @since 2026/11/1
     */
    @PostMapping("chooseUser/{groupId}")
    public R<PageInfo<MsgGroupMemberVo>> chooseUser(@PathVariable Long groupId, @RequestBody PageRequest<MsgGroupMemberVo> pageRequest) {
        PageInfo<MsgGroupMemberVo> msgGroupMemberVoPageInfo = msgGroupService.chooseUser(groupId, pageRequest);
        return R.ok(msgGroupMemberVoPageInfo);
    }


    /**
     * 未选择的用户
     *
     * @param groupId 群组id
     * @param request 分页参数
     * @return
     * @since 2026/11/1
     */
    @PostMapping("unChooseUser/{groupId}")
    public R<PageInfo<SysUserVo>> unChooseUser(@PathVariable Long groupId, @RequestBody PageRequest<SysUserVo> request) {
        PageInfo<SysUserVo> sysUserVoPageInfo = msgGroupService.unChooseUser(groupId, request);
        return R.ok(sysUserVoPageInfo);
    }

    /**
     * 把用户导入群组。
     *
     * @param groupId 群组 id
     * @param userIds 用户 id
     */
    @SaCheckPermission(value = {"msg:group:add", "msg:group:edit"}, mode = SaMode.OR)
    @PostMapping("importUser/{groupId}")
    public R<Void> importUser(@PathVariable Long groupId, @RequestBody List<Long> userIds) {
        msgGroupService.importUser(groupId, userIds);
        return R.ok();
    }

    /**
     * 删除已选用户成员。
     *
     * @param groupId   群组 id
     * @param memberIds 群组成员主键
     */
    @SaCheckPermission(value = {"msg:group:add", "msg:group:edit"}, mode = SaMode.OR)
    @PostMapping("removeUser/{groupId}")
    public R<Void> removeUser(@PathVariable Long groupId, @RequestBody List<Long> memberIds) {
        msgGroupService.removeUser(groupId, memberIds);
        return R.ok();
    }

    /**
     * 候选部门树的扁平数据。已选部门 {@code chosen=true}。
     *
     * @param groupId 群组 id
     * @return 部门节点
     */
    @PostMapping("deptTree/{groupId}")
    public R<List<MsgGroupDeptNode>> deptTree(@PathVariable Long groupId) {
        return R.ok(msgGroupService.deptTree(groupId));
    }

    /**
     * 按勾选结果保存部门成员。
     *
     * @param groupId 群组 id
     * @param deptIds 勾选的部门 id
     */
    @SaCheckPermission(value = {"msg:group:add", "msg:group:edit"}, mode = SaMode.OR)
    @PostMapping("saveDept/{groupId}")
    public R<Void> saveDept(@PathVariable Long groupId, @RequestBody List<Long> deptIds) {
        msgGroupService.saveDept(groupId, deptIds);
        return R.ok();
    }

    /**
     * 已添加的外部人。
     *
     * @param groupId     群组 id
     * @param pageRequest 分页参数
     * @return 外部人成员
     */
    @PostMapping("pageExternal/{groupId}")
    public R<PageInfo<MsgGroupMemberVo>> pageExternal(@PathVariable Long groupId,
                                                      @RequestBody PageRequest<MsgGroupMemberVo> pageRequest) {
        return R.ok(msgGroupService.pageExternal(groupId, pageRequest));
    }

    /**
     * 添加外部人。
     *
     * @param groupId 群组 id
     * @param body    名称、手机、邮箱
     */
    @SaCheckPermission(value = {"msg:group:add", "msg:group:edit"}, mode = SaMode.OR)
    @PostMapping("addExternal/{groupId}")
    public R<Void> addExternal(@PathVariable Long groupId, @RequestBody MsgGroupMemberVo body) {
        msgGroupService.addExternal(groupId, body);
        return R.ok();
    }

    /**
     * 删除已添加的外部人。
     *
     * @param groupId   群组 id
     * @param memberIds 群组成员主键
     */
    @SaCheckPermission(value = {"msg:group:add", "msg:group:edit"}, mode = SaMode.OR)
    @PostMapping("removeExternal/{groupId}")
    public R<Void> removeExternal(@PathVariable Long groupId, @RequestBody List<Long> memberIds) {
        msgGroupService.removeExternal(groupId, memberIds);
        return R.ok();
    }

}
