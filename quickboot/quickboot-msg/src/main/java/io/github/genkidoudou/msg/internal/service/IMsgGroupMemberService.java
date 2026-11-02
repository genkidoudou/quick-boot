package io.github.genkidoudou.msg.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.msg.internal.entity.MsgGroupMember;
import io.github.genkidoudou.msg.internal.vo.MsgGroupMemberVo;

import java.util.List;

/**
 * 群组成员服务。
 */
public interface IMsgGroupMemberService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<MsgGroupMemberVo> pageVo(PageRequest<MsgGroupMemberVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    MsgGroupMemberVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    MsgGroupMember saveVo(MsgGroupMemberVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(MsgGroupMemberVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 根据群组id查询
     *
     * @param groupId 群组id
     * @return
     * @since 2026/11/1
     */
    List<MsgGroupMemberVo> listByGroupId(Long groupId);

}
