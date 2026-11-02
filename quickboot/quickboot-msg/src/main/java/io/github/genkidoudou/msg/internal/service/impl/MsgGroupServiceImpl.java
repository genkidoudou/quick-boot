package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgGroup;
import io.github.genkidoudou.msg.internal.mapper.MsgGroupMapper;
import io.github.genkidoudou.msg.internal.service.IMsgGroupMemberService;
import io.github.genkidoudou.msg.internal.service.IMsgGroupService;
import io.github.genkidoudou.msg.internal.vo.MsgGroupDeptNode;
import io.github.genkidoudou.msg.internal.vo.MsgGroupMemberVo;
import io.github.genkidoudou.msg.internal.vo.MsgGroupVo;
import io.github.genkidoudou.system.api.api.SysDeptApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysDeptOptionVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息群组服务实现。
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MsgGroupServiceImpl extends BaseVoServiceImpl<MsgGroupMapper, MsgGroup, MsgGroupVo>
        implements IMsgGroupService {

    /** 用户群组。 */
    private static final String TYPE_USER = "1";

    /** 部门群组。 */
    private static final String TYPE_DEPT = "2";

    /** 外部人群组。 */
    private static final String TYPE_EXTERNAL = "3";

    private final IMsgGroupMemberService msgGroupMemberService;

    private final SysUserApi sysUserApi;

    private final SysDeptApi sysDeptApi;

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<MsgGroup> query, MsgGroupVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getName()), MsgGroup::getName, param.getName());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public MsgGroupVo getVoById(Long id) {
        MsgGroupVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "消息群组不存在");
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
    public MsgGroup saveVo(MsgGroupVo vo) {
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。成员类型以库里的为准，不允许改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(MsgGroupVo vo) {
        MsgGroup existing = super.getById(vo.getId());
        if (existing == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "消息群组不存在");
        }
        vo.setMemberType(existing.getMemberType());
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

    public PageInfo<MsgGroupMemberVo> chooseUser(Long groupId, PageRequest<MsgGroupMemberVo> groupPageRequest) {
        MsgGroup msgGroup = super.getById(groupId);
        if (null == msgGroup) {
            throw new WarningException(30001);
        }
        MsgGroupMemberVo param = groupPageRequest.getParam();
        if (null == param) {
            param = new MsgGroupMemberVo();
            groupPageRequest.setParam(param);
        }
        param.setGroupId(groupId);
        PageInfo<MsgGroupMemberVo> msgGroupMemberVoPageInfo = msgGroupMemberService.pageVo(groupPageRequest);
        List<MsgGroupMemberVo> records = msgGroupMemberVoPageInfo.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            List<Long> userIds = records.stream().filter(a -> a.getUserId() != null).map(MsgGroupMemberVo::getUserId).distinct().toList();
            Map<Long, SysUserVo> userVoMap = sysUserApi.listByUserIds(userIds).stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            for (MsgGroupMemberVo record : records) {
                SysUserVo sysUserVo = userVoMap.get(record.getUserId());
                if (null != sysUserVo) {
                    record.setUserName(sysUserVo.getUserName());
                    record.setUserStatus(sysUserVo.getStatus());
                }
            }
            // sysUserApi.listByUserIds(userIds).stream().collect(Collectors.toMap(k -> k.getId(), v -> v));

        }

        return msgGroupMemberVoPageInfo;
    }

    @Override
    public PageInfo<SysUserVo> unChooseUser(Long groupId, PageRequest<SysUserVo> request) {
        List<MsgGroupMemberVo> msgGroupMemberVos = msgGroupMemberService.listByGroupId(groupId);
        List<Long> userIds = msgGroupMemberVos.stream()
                .map(MsgGroupMemberVo::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return sysUserApi.page(request, userIds);
    }

    /**
     * 仅用户群组可导入。已存在的用户不再插入。
     *
     * @param groupId 群组 id
     * @param userIds 用户 id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importUser(Long groupId, List<Long> userIds) {
        MsgGroup group = requireUserGroup(groupId);
        if (CollectionUtil.isEmpty(userIds)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请选择用户");
        }
        Set<Long> exists = msgGroupMemberService.listByGroupId(group.getId()).stream()
                .map(MsgGroupMemberVo::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (Long userId : userIds.stream().filter(Objects::nonNull).distinct().toList()) {
            if (exists.contains(userId)) {
                continue;
            }
            MsgGroupMemberVo member = new MsgGroupMemberVo();
            member.setGroupId(group.getId());
            member.setMemberType(TYPE_USER);
            member.setUserId(userId);
            msgGroupMemberService.saveVo(member);
        }
    }

    /**
     * 只删除属于该群组的成员行。
     *
     * @param groupId   群组 id
     * @param memberIds 群组成员主键
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(Long groupId, List<Long> memberIds) {
        requireUserGroup(groupId);
        if (CollectionUtil.isEmpty(memberIds)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请选择要删除的成员");
        }
        List<Long> ids = memberIds.stream().filter(Objects::nonNull).distinct().toList();
        Set<Long> allowed = msgGroupMemberService.listByGroupId(groupId).stream()
                .map(MsgGroupMemberVo::getId)
                .collect(Collectors.toSet());
        if (!allowed.containsAll(ids)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "成员不属于该群组");
        }
        msgGroupMemberService.removeByIds(ids);
    }

    /**
     * 群组必须存在，且成员类型为用户。历史数据没填类型时，按用户组补上。
     *
     * @param groupId 群组 id
     * @return 群组
     */
    private MsgGroup requireUserGroup(Long groupId) {
        MsgGroup group = super.getById(groupId);
        if (group == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "消息群组不存在");
        }
        if (StrUtil.isBlank(group.getMemberType())) {
            group.setMemberType(TYPE_USER);
            super.updateById(group);
            return group;
        }
        if (!TYPE_USER.equals(group.getMemberType())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "只有用户群组可以维护用户成员");
        }
        return group;
    }

    /**
     * 全部部门，并标出已在组内的部门。
     *
     * @param groupId 群组 id
     * @return 扁平部门列表
     */
    @Override
    public List<MsgGroupDeptNode> deptTree(Long groupId) {
        requireDeptGroup(groupId);
        Set<Long> chosen = msgGroupMemberService.listByGroupId(groupId).stream()
                .map(MsgGroupMemberVo::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return sysDeptApi.listAll().stream().map(dept -> {
            MsgGroupDeptNode node = new MsgGroupDeptNode();
            node.setId(dept.getId());
            node.setParentId(dept.getParentId());
            node.setDeptName(dept.getDeptName());
            node.setStatus(dept.getStatus());
            node.setOrderNum(dept.getOrderNum());
            node.setChosen(dept.getId() != null && chosen.contains(dept.getId()));
            return node;
        }).toList();
    }

    /**
     * 按勾选结果覆盖部门成员。
     *
     * @param groupId 群组 id
     * @param deptIds 勾选的部门 id，可为空
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDept(Long groupId, List<Long> deptIds) {
        MsgGroup group = requireDeptGroup(groupId);
        List<Long> target = deptIds == null
                ? List.of()
                : deptIds.stream().filter(Objects::nonNull).distinct().toList();
        Set<Long> known = sysDeptApi.listAll().stream()
                .map(SysDeptOptionVo::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (target.stream().anyMatch(id -> !known.contains(id))) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "部门不存在");
        }
        List<MsgGroupMemberVo> existing = msgGroupMemberService.listByGroupId(group.getId());
        Set<Long> targetSet = Set.copyOf(target);
        List<Long> removeIds = existing.stream()
                .filter(member -> member.getDeptId() == null || !targetSet.contains(member.getDeptId()))
                .map(MsgGroupMemberVo::getId)
                .filter(Objects::nonNull)
                .toList();
        if (CollectionUtil.isNotEmpty(removeIds)) {
            msgGroupMemberService.removeByIds(removeIds);
        }
        Set<Long> exists = existing.stream()
                .map(MsgGroupMemberVo::getDeptId)
                .filter(targetSet::contains)
                .collect(Collectors.toSet());
        for (Long deptId : target) {
            if (exists.contains(deptId)) {
                continue;
            }
            MsgGroupMemberVo member = new MsgGroupMemberVo();
            member.setGroupId(group.getId());
            member.setMemberType(TYPE_DEPT);
            member.setDeptId(deptId);
            msgGroupMemberService.saveVo(member);
        }
    }

    /**
     * 群组必须存在，且成员类型为部门。
     *
     * @param groupId 群组 id
     * @return 群组
     */
    private MsgGroup requireDeptGroup(Long groupId) {
        MsgGroup group = super.getById(groupId);
        if (group == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "消息群组不存在");
        }
        if (!TYPE_DEPT.equals(group.getMemberType())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "只有部门群组可以维护部门成员");
        }
        return group;
    }

    /**
     * 已添加的外部人。
     *
     * @param groupId     群组 id
     * @param pageRequest 分页参数
     * @return 外部人成员
     */
    @Override
    public PageInfo<MsgGroupMemberVo> pageExternal(Long groupId, PageRequest<MsgGroupMemberVo> pageRequest) {
        requireExternalGroup(groupId);
        MsgGroupMemberVo param = pageRequest.getParam();
        if (param == null) {
            param = new MsgGroupMemberVo();
            pageRequest.setParam(param);
        }
        param.setGroupId(groupId);
        param.setMemberType(TYPE_EXTERNAL);
        return msgGroupMemberService.pageVo(pageRequest);
    }

    /**
     * 添加外部人。名称和联系方式都相同的不再插入。
     *
     * @param groupId 群组 id
     * @param body    名称、手机、邮箱
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addExternal(Long groupId, MsgGroupMemberVo body) {
        MsgGroup group = requireExternalGroup(groupId);
        String name = body == null ? null : StrUtil.trim(body.getName());
        String phone = body == null ? null : StrUtil.trim(body.getPhone());
        String email = body == null ? null : StrUtil.trim(body.getEmail());
        MsgGroupMemberVo member = new MsgGroupMemberVo();
        member.setGroupId(group.getId());
        member.setMemberType(TYPE_EXTERNAL);
        member.setName(name);
        member.setPhone(StrUtil.emptyToNull(phone));
        member.setEmail(StrUtil.emptyToNull(email));
        boolean duplicated = msgGroupMemberService.listByGroupId(group.getId()).stream()
                .anyMatch(item -> sameText(name, item.getName())
                        && sameText(phone, item.getPhone())
                        && sameText(email, item.getEmail()));
        if (duplicated) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "这个外部人已经在群组里");
        }
        msgGroupMemberService.saveVo(member);
    }

    /**
     * 删除属于该外部人群组的成员。
     *
     * @param groupId   群组 id
     * @param memberIds 群组成员主键
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeExternal(Long groupId, List<Long> memberIds) {
        requireExternalGroup(groupId);
        if (CollectionUtil.isEmpty(memberIds)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请选择要删除的成员");
        }
        List<Long> ids = memberIds.stream().filter(Objects::nonNull).distinct().toList();
        Set<Long> allowed = msgGroupMemberService.listByGroupId(groupId).stream()
                .map(MsgGroupMemberVo::getId)
                .collect(Collectors.toSet());
        if (!allowed.containsAll(ids)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "成员不属于该群组");
        }
        msgGroupMemberService.removeByIds(ids);
    }

    /**
     * 群组必须存在，且成员类型为外部人。
     *
     * @param groupId 群组 id
     * @return 群组
     */
    private MsgGroup requireExternalGroup(Long groupId) {
        MsgGroup group = super.getById(groupId);
        if (group == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "消息群组不存在");
        }
        if (!TYPE_EXTERNAL.equals(group.getMemberType())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "只有外部人群组可以维护外部人");
        }
        return group;
    }

    /**
     * 空白与 {@code null} 视为相同。
     *
     * @param left  左值
     * @param right 右值
     * @return 是否相同
     */
    private static boolean sameText(String left, String right) {
        return StrUtil.equals(StrUtil.trim(StrUtil.nullToEmpty(left)), StrUtil.trim(StrUtil.nullToEmpty(right)));
    }
}
