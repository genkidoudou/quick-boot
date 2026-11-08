package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgChannel;
import io.github.genkidoudou.msg.internal.mapper.MsgChannelMapper;
import io.github.genkidoudou.msg.internal.service.IMsgChannelService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息渠道服务实现。
 */
@Service
public class MsgChannelServiceImpl extends BaseVoServiceImpl<MsgChannelMapper, MsgChannel, MsgChannelVo>
        implements IMsgChannelService {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<MsgChannel> query, MsgChannelVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getName()), MsgChannel::getName, param.getName())
                .eq(StrUtil.isNotBlank(param.getChannelType()), MsgChannel::getChannelType, param.getChannelType())
                .like(StrUtil.isNotBlank(param.getBeanName()), MsgChannel::getBeanName, param.getBeanName())
                .eq(StrUtil.isNotBlank(param.getStatus()), MsgChannel::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public MsgChannelVo getVoById(Long id) {
        MsgChannelVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "消息渠道不存在");
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
    public MsgChannel saveVo(MsgChannelVo vo) {
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
    public boolean updateVoById(MsgChannelVo vo) {
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

    @Override
    public List<MsgChannelVo> listVoByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return List.of();
        }
        return BeanUtil.copyToList(super.listByIds(ids), MsgChannelVo.class);
    }
}
