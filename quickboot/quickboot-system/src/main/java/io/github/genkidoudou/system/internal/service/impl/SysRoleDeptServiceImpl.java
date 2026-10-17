package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysRoleDept;
import io.github.genkidoudou.system.internal.mapper.SysRoleDeptMapper;
import io.github.genkidoudou.system.internal.service.ISysRoleDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色部门绑定服务实现。
 */
@Service
public class SysRoleDeptServiceImpl extends ServiceImpl<SysRoleDeptMapper, SysRoleDept>
        implements ISysRoleDeptService {

    /**
     * 按角色主键查询部门 id 列表。
     *
     * @param roleId 角色主键
     * @return 部门 id 列表
     */
    @Override
    public List<Long> listDeptIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId))
                .stream()
                .map(SysRoleDept::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 按角色主键集合查询部门 id 列表。
     *
     * @param roleIds 角色主键集合
     * @return 部门 id 列表
     */
    @Override
    public List<Long> listDeptIdsByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return list(new LambdaQueryWrapper<SysRoleDept>().in(SysRoleDept::getRoleId, roleIds))
                .stream()
                .map(SysRoleDept::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 覆盖角色与部门的关联。
     *
     * @param roleId  角色主键
     * @param deptIds 部门 id 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceDeptIds(Long roleId, List<Long> deptIds) {
        remove(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId));
        if (CollectionUtil.isEmpty(deptIds)) {
            return;
        }
        List<SysRoleDept> rows = deptIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(deptId -> {
                    SysRoleDept row = new SysRoleDept();
                    row.setRoleId(roleId);
                    row.setDeptId(deptId);
                    return row;
                })
                .collect(Collectors.toList());
        saveBatch(rows);
    }

    /**
     * 按角色主键集合删除关联。
     *
     * @param roleIds 角色主键集合
     */
    @Override
    public void deleteByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        remove(new LambdaQueryWrapper<SysRoleDept>().in(SysRoleDept::getRoleId, roleIds));
    }
}
