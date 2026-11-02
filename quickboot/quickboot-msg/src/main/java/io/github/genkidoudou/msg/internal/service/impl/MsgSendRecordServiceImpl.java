package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecord;
import io.github.genkidoudou.msg.internal.mapper.MsgSendRecordMapper;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordService;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息发送记录服务实现。
 */
@Service
public class MsgSendRecordServiceImpl extends BaseVoServiceImpl<MsgSendRecordMapper, MsgSendRecord, MsgSendRecordVo>
        implements IMsgSendRecordService {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<MsgSendRecord> query, MsgSendRecordVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getTitle()), MsgSendRecord::getTitle, param.getTitle())
                .eq(StrUtil.isNotBlank(param.getSource()), MsgSendRecord::getSource, param.getSource())
                .eq(StrUtil.isNotBlank(param.getStatus()), MsgSendRecord::getStatus, param.getStatus())
                .eq(param.getApiKeyId() != null, MsgSendRecord::getApiKeyId, param.getApiKeyId())
                .eq(param.getUserId() != null, MsgSendRecord::getUserId, param.getUserId());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public MsgSendRecordVo getVoById(Long id) {
        MsgSendRecordVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "发送记录不存在");
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
    public MsgSendRecord saveVo(MsgSendRecordVo vo) {
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
    public boolean updateVoById(MsgSendRecordVo vo) {
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
