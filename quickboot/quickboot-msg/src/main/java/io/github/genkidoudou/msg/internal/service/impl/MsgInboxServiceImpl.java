package io.github.genkidoudou.msg.internal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgInbox;
import io.github.genkidoudou.msg.internal.mapper.MsgInboxMapper;
import io.github.genkidoudou.msg.internal.service.IMsgInboxService;
import io.github.genkidoudou.msg.internal.vo.MsgInboxVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 站内信收件箱服务实现。
 */
@Service
public class MsgInboxServiceImpl extends BaseVoServiceImpl<MsgInboxMapper, MsgInbox, MsgInboxVo>
        implements IMsgInboxService {

    private static final String UNREAD = "0";

    private static final String READ = "1";

    /**
     * 按查询条件组装 Wrapper。只查当前登录人，新消息在前。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<MsgInbox> query, MsgInboxVo param) {
        query.eq(MsgInbox::getUserId, StpUtil.getLoginIdAsLong());
        if (param != null) {
            query.like(StrUtil.isNotBlank(param.getTitle()), MsgInbox::getTitle, param.getTitle())
                    .eq(StrUtil.isNotBlank(param.getReadFlag()), MsgInbox::getReadFlag, param.getReadFlag())
                    .eq(param.getChannelId() != null, MsgInbox::getChannelId, param.getChannelId())
                    .eq(param.getRecordId() != null, MsgInbox::getRecordId, param.getRecordId());
        }
        query.orderByDesc(MsgInbox::getCreateTime);
    }

    @Override
    public long unreadCount() {
        return count(new LambdaQueryWrapper<MsgInbox>()
                .eq(MsgInbox::getUserId, StpUtil.getLoginIdAsLong())
                .eq(MsgInbox::getReadFlag, UNREAD));
    }

    @Override
    public void markRead(Long id) {
        MsgInbox inbox = id == null ? null : getById(id);
        if (inbox == null || !Objects.equals(inbox.getUserId(), StpUtil.getLoginIdAsLong())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "站内信不存在");
        }
        if (READ.equals(inbox.getReadFlag())) {
            return;
        }
        MsgInbox update = new MsgInbox();
        update.setId(id);
        update.setReadFlag(READ);
        updateById(update);
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public MsgInboxVo getVoById(Long id) {
        MsgInboxVo vo = super.getVoById(id);
        if (vo == null || !Objects.equals(vo.getUserId(), StpUtil.getLoginIdAsLong())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "站内信不存在");
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
    public MsgInbox saveVo(MsgInboxVo vo) {
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
    public boolean updateVoById(MsgInboxVo vo) {
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
    public void saveBatch(List<MsgInbox> msgInboxes) {
        if (CollectionUtil.isEmpty(msgInboxes)) {
            return;
        }
        super.saveBatch(msgInboxes);
    }
}
