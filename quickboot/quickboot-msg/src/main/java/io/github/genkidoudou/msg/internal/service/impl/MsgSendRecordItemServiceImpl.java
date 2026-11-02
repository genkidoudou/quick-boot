package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecordItem;
import io.github.genkidoudou.msg.internal.mapper.MsgSendRecordItemMapper;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordItemService;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordItemVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息发送明细服务实现。
 */
@Service
public class MsgSendRecordItemServiceImpl
        extends BaseVoServiceImpl<MsgSendRecordItemMapper, MsgSendRecordItem, MsgSendRecordItemVo>
        implements IMsgSendRecordItemService {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<MsgSendRecordItem> query, MsgSendRecordItemVo param) {
        if (param == null) {
            return;
        }
        query.eq(param.getRecordId() != null, MsgSendRecordItem::getRecordId, param.getRecordId())
                .eq(param.getChannelId() != null, MsgSendRecordItem::getChannelId, param.getChannelId())
                .eq(StrUtil.isNotBlank(param.getTargetType()), MsgSendRecordItem::getTargetType, param.getTargetType())
                .eq(param.getGroupId() != null, MsgSendRecordItem::getGroupId, param.getGroupId())
                .eq(param.getUserId() != null, MsgSendRecordItem::getUserId, param.getUserId())
                .eq(param.getDeptId() != null, MsgSendRecordItem::getDeptId, param.getDeptId())
                .like(StrUtil.isNotBlank(param.getName()), MsgSendRecordItem::getName, param.getName())
                .eq(StrUtil.isNotBlank(param.getStatus()), MsgSendRecordItem::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public MsgSendRecordItemVo getVoById(Long id) {
        MsgSendRecordItemVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "发送明细不存在");
        }
        return vo;
    }

    /**
     * 新增；带事务。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgSendRecordItem saveVo(MsgSendRecordItemVo vo) {
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(MsgSendRecordItemVo vo) {
        return super.updateVoById(vo);
    }

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    @Override
    public void removeByIds(List<Long> ids) {
        deleteByIds(ids);
    }
}
