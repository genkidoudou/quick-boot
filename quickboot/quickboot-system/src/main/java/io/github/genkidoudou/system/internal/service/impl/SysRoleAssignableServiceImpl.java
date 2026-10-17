package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysRoleAssignable;
import io.github.genkidoudou.system.internal.mapper.SysRoleAssignableMapper;
import io.github.genkidoudou.system.internal.service.ISysRoleAssignableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 可授权角色绑定服务实现。
 */
@Service
public class SysRoleAssignableServiceImpl extends ServiceImpl<SysRoleAssignableMapper, SysRoleAssignable>
        implements ISysRoleAssignableService {

    /**
     * 按角色主键查询可授权角色 id 列表。
     *
     * @param roleId 角色主键
     * @return 可授权角色 id 列表
     */
    @Override
    public List<Long> listAssignableRoleIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<SysRoleAssignable>().eq(SysRoleAssignable::getRoleId, roleId))
                .stream()
                .map(SysRoleAssignable::getAssignableRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 覆盖角色与可授权角色的关联。
     *
     * @param roleId            角色主键
     * @param assignableRoleIds 可授权角色 id 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceAssignableRoleIds(Long roleId, List<Long> assignableRoleIds) {
        remove(new LambdaQueryWrapper<SysRoleAssignable>().eq(SysRoleAssignable::getRoleId, roleId));
        if (CollectionUtil.isEmpty(assignableRoleIds)) {
            return;
        }
        List<SysRoleAssignable> rows = assignableRoleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(rid -> {
                    SysRoleAssignable row = new SysRoleAssignable();
                    row.setRoleId(roleId);
                    row.setAssignableRoleId(rid);
                    return row;
                })
                .collect(Collectors.toList());
        saveBatch(rows);
    }

    @Override
    public List<Long> listAssignableRoleIdsByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return super.baseMapper.selectList(new LambdaQueryWrapper<SysRoleAssignable>()
                        .in(SysRoleAssignable::getRoleId, roleIds))
                .stream()
                .map(SysRoleAssignable::getAssignableRoleId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        this.baseMapper.delete(new LambdaQueryWrapper<SysRoleAssignable>()
                .in(SysRoleAssignable::getRoleId, roleIds));
        
    }
}
