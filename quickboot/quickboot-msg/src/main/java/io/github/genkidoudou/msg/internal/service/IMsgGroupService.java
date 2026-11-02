package io.github.genkidoudou.msg.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.msg.internal.entity.MsgGroup;
import io.github.genkidoudou.msg.internal.vo.MsgGroupDeptNode;
import io.github.genkidoudou.msg.internal.vo.MsgGroupMemberVo;
import io.github.genkidoudou.msg.internal.vo.MsgGroupVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;

import java.util.List;

/**
 * 消息群组服务。
 */
public interface IMsgGroupService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<MsgGroupVo> pageVo(PageRequest<MsgGroupVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    MsgGroupVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    MsgGroup saveVo(MsgGroupVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(MsgGroupVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 已经选择的用户
     *
     * @param groupId          分组id
     * @param groupPageRequest 分页参数
     * @return
     * @since 2026/11/1
     */
    PageInfo<MsgGroupMemberVo> chooseUser(Long groupId, PageRequest<MsgGroupMemberVo> groupPageRequest);

    /**
     * 未选择的用户
     *
     * @param groupId 群组id
     * @param request 分页参数
     * @return
     * @since 2026/11/1
     */
    PageInfo<SysUserVo> unChooseUser(Long groupId, PageRequest<SysUserVo> request);

    /**
     * 把用户导入用户类型群组。已在组内的用户跳过。
     *
     * @param groupId 群组 id
     * @param userIds 用户 id
     */
    void importUser(Long groupId, List<Long> userIds);

    /**
     * 按成员主键删除，且这些成员必须属于该群组。
     *
     * @param groupId   群组 id
     * @param memberIds 群组成员主键
     */
    void removeUser(Long groupId, List<Long> memberIds);

    /**
     * 候选部门。已在组内的节点 {@code chosen=true}。
     *
     * @param groupId 群组 id
     * @return 扁平部门列表，由调用方组树
     */
    List<MsgGroupDeptNode> deptTree(Long groupId);

    /**
     * 按勾选结果保存部门成员。未勾选的部门会从群组中去掉。
     *
     * @param groupId 群组 id
     * @param deptIds 勾选的部门 id，可为空
     */
    void saveDept(Long groupId, List<Long> deptIds);

    /**
     * 已添加的外部人。
     *
     * @param groupId     群组 id
     * @param pageRequest 分页参数
     * @return 外部人成员
     */
    PageInfo<MsgGroupMemberVo> pageExternal(Long groupId, PageRequest<MsgGroupMemberVo> pageRequest);

    /**
     * 添加一名外部人。名称必填，手机或邮箱至少一项。
     *
     * @param groupId 群组 id
     * @param body    名称、手机、邮箱
     */
    void addExternal(Long groupId, MsgGroupMemberVo body);

    /**
     * 删除已添加的外部人，且这些成员必须属于该群组。
     *
     * @param groupId   群组 id
     * @param memberIds 群组成员主键
     */
    void removeExternal(Long groupId, List<Long> memberIds);
}
