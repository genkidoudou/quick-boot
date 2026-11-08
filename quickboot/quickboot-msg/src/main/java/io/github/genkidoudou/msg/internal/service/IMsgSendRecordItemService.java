package io.github.genkidoudou.msg.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecordItem;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordItemVo;

import java.util.Collection;
import java.util.List;

/**
 * 消息发送明细服务。
 */
public interface IMsgSendRecordItemService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<MsgSendRecordItemVo> pageVo(PageRequest<MsgSendRecordItemVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    MsgSendRecordItemVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    MsgSendRecordItem saveVo(MsgSendRecordItemVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(MsgSendRecordItemVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 按发送记录主键查出全部明细。
     *
     * @param recordIds 发送记录主键
     * @return 明细，无记录时为空列表
     */
    List<MsgSendRecordItem> listByRecordIds(Collection<Long> recordIds);
}
