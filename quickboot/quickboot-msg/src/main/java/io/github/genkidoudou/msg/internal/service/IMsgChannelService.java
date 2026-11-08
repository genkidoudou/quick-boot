package io.github.genkidoudou.msg.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.msg.internal.entity.MsgChannel;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;

import java.util.List;

/**
 * 消息渠道服务。
 */
public interface IMsgChannelService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<MsgChannelVo> pageVo(PageRequest<MsgChannelVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    MsgChannelVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    MsgChannel saveVo(MsgChannelVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(MsgChannelVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 按主键批量查询渠道。
     *
     * @param ids 渠道主键
     * @return 渠道，无记录时为空列表
     */
    List<MsgChannelVo> listVoByIds(List<Long> ids);
}
