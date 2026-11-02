package io.github.genkidoudou.msg.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecord;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordVo;

import java.util.List;

/**
 * 消息发送记录服务。
 */
public interface IMsgSendRecordService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<MsgSendRecordVo> pageVo(PageRequest<MsgSendRecordVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    MsgSendRecordVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    MsgSendRecord saveVo(MsgSendRecordVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(MsgSendRecordVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);
}
