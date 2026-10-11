package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysUserRole;
import io.github.genkidoudou.system.internal.mapper.SysUserRoleMapper;
import io.github.genkidoudou.system.internal.service.ISysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户角色绑定服务实现。
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements ISysUserRoleService {

    /**
     * 按用户主键查询角色 id 列表。
     *
     * @param userId 用户主键
     * @return 角色 id 列表
     */
    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 覆盖用户与角色的关联。
     *
     * @param userId  用户主键
     * @param roleIds 角色 id 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceRoleIds(Long userId, List<Long> roleIds) {
        remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        List<SysUserRole> rows = roleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(rid -> {
                    SysUserRole row = new SysUserRole();
                    row.setUserId(userId);
                    row.setRoleId(rid);
                    return row;
                })
                .collect(Collectors.toList());
        saveBatch(rows);
    }
}
