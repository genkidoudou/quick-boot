package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecord;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecordItem;
import io.github.genkidoudou.msg.internal.mapper.MsgSendRecordMapper;
import io.github.genkidoudou.msg.internal.service.IMsgChannelService;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordItemService;
import io.github.genkidoudou.msg.internal.service.IMsgSendRecordService;
import io.github.genkidoudou.msg.internal.vo.MsgChannelVo;
import io.github.genkidoudou.msg.internal.vo.MsgSendRecordVo;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息发送记录服务实现。
 */
@Service
@RequiredArgsConstructor
public class MsgSendRecordServiceImpl extends BaseVoServiceImpl<MsgSendRecordMapper, MsgSendRecord, MsgSendRecordVo>
        implements IMsgSendRecordService {

    private static final String TARGET_USER = "1";

    private final IMsgSendRecordItemService msgSendRecordItemService;

    private final IMsgChannelService msgChannelService;

    private final SysUserApi sysUserApi;

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public PageInfo<MsgSendRecordVo> pageVo(PageRequest<MsgSendRecordVo> pageRequest) {
        PageInfo<MsgSendRecordVo> page = super.pageVo(pageRequest);
        fillDisplay(page.getRecords());
        return page;
    }

    /**
     * 用户列展示收件人，不展示操作人 id。渠道取本次明细上的渠道名称。
     */
    private void fillDisplay(List<MsgSendRecordVo> records) {
        if (CollectionUtil.isEmpty(records)) {
            return;
        }
        List<Long> recordIds = records.stream().map(MsgSendRecordVo::getId).filter(Objects::nonNull).toList();
        List<MsgSendRecordItem> items = msgSendRecordItemService.listByRecordIds(recordIds);
        Map<Long, List<MsgSendRecordItem>> byRecord = items.stream()
                .collect(Collectors.groupingBy(MsgSendRecordItem::getRecordId));
        Map<Long, String> channelNames = channelNames(items);
        Map<Long, String> userNames = userNames(items);
        for (MsgSendRecordVo record : records) {
            List<MsgSendRecordItem> recordItems = byRecord.getOrDefault(record.getId(), List.of());
            record.setChannelName(join(recordItems.stream().map(item -> channelNames.get(item.getChannelId())).toList()));
            record.setReceiverNames(join(receiverLabels(recordItems, userNames)));
        }
    }

    private List<String> receiverLabels(List<MsgSendRecordItem> items, Map<Long, String> userNames) {
        List<String> labels = new ArrayList<>();
        for (MsgSendRecordItem item : items) {
            if (TARGET_USER.equals(item.getTargetType())) {
                labels.add(userNames.getOrDefault(item.getUserId(), item.getUserId() == null ? "" : String.valueOf(item.getUserId())));
            } else if (StrUtil.isNotBlank(item.getName())) {
                labels.add(item.getName());
            }
        }
        return labels;
    }

    private Map<Long, String> channelNames(List<MsgSendRecordItem> items) {
        List<Long> ids = items.stream().map(MsgSendRecordItem::getChannelId).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> names = new HashMap<>();
        for (MsgChannelVo channel : msgChannelService.listVoByIds(ids)) {
            names.put(channel.getId(), channel.getName());
        }
        return names;
    }

    private Map<Long, String> userNames(List<MsgSendRecordItem> items) {
        List<Long> ids = items.stream()
                .filter(item -> TARGET_USER.equals(item.getTargetType()))
                .map(MsgSendRecordItem::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<SysUserVo> users = sysUserApi.listByUserIds(ids);
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

    private String join(List<String> values) {
        Set<String> unique = new LinkedHashSet<>();
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                unique.add(value);
            }
        }
        return String.join("、", unique);
    }

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
