package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecordItem;
import io.github.genkidoudou.msg.internal.mapper.MsgSendRecordItemMapper;
import io.github.genkidoudou.msg.internal.service.IMsgChannelService;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordItemService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordItemVo;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 消息发送明细服务实现。
 */
@Service
@RequiredArgsConstructor
public class MsgSendRecordItemServiceImpl
        extends BaseVoServiceImpl<MsgSendRecordItemMapper, MsgSendRecordItem, MsgSendRecordItemVo>
        implements IMsgSendRecordItemService {

    private static final String TARGET_USER = "1";

    private final IMsgChannelService msgChannelService;

    private final SysUserApi sysUserApi;

    @Override
    public PageInfo<MsgSendRecordItemVo> pageVo(PageRequest<MsgSendRecordItemVo> pageRequest) {
        PageInfo<MsgSendRecordItemVo> page = super.pageVo(pageRequest);
        fillDisplay(page.getRecords());
        return page;
    }

    @Override
    public MsgSendRecordItemVo getVoById(Long id) {
        MsgSendRecordItemVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "发送明细不存在");
        }
        fillDisplay(List.of(vo));
        return vo;
    }

    @Override
    public List<MsgSendRecordItem> listByRecordIds(Collection<Long> recordIds) {
        if (CollectionUtil.isEmpty(recordIds)) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<MsgSendRecordItem>().in(MsgSendRecordItem::getRecordId, recordIds));
    }

    /**
     * 渠道显示名称；用户对象的名称列补上昵称 / 用户名。
     */
    private void fillDisplay(List<MsgSendRecordItemVo> records) {
        if (CollectionUtil.isEmpty(records)) {
            return;
        }
        Map<Long, String> channelNames = channelNames(records.stream().map(MsgSendRecordItemVo::getChannelId).toList());
        Map<Long, String> userNames = userNames(records.stream()
                .filter(item -> TARGET_USER.equals(item.getTargetType()))
                .map(MsgSendRecordItemVo::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList());
        for (MsgSendRecordItemVo item : records) {
            item.setChannelName(channelNames.get(item.getChannelId()));
            if (TARGET_USER.equals(item.getTargetType()) && StrUtil.isBlank(item.getName())) {
                item.setName(userNames.get(item.getUserId()));
            }
        }
    }

    private Map<Long, String> channelNames(List<Long> channelIds) {
        List<Long> ids = channelIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        Map<Long, String> names = new HashMap<>();
        for (MsgChannelVo channel : msgChannelService.listVoByIds(ids)) {
            names.put(channel.getId(), channel.getName());
        }
        return names;
    }

    private Map<Long, String> userNames(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Map.of();
        }
        List<SysUserVo> users = sysUserApi.listByUserIds(userIds);
        if (CollectionUtil.isEmpty(users)) {
            return Map.of();
        }
        return users.stream().collect(Collectors.toMap(SysUserVo::getId, this::userLabel, (left, right) -> left));
    }

    private String userLabel(SysUserVo user) {
        String nickName = StrUtil.blankToDefault(user.getNickName(), "");
        String userName = StrUtil.blankToDefault(user.getUserName(), "");
        if (StrUtil.isNotBlank(nickName) && StrUtil.isNotBlank(userName) && !nickName.equals(userName)) {
            return nickName + " / " + userName;
        }
        return StrUtil.blankToDefault(nickName, userName);
    }

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
