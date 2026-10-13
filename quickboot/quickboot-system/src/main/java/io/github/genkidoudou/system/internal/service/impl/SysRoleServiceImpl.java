package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.system.internal.entity.SysRole;
import io.github.genkidoudou.system.internal.entity.SysUser;
import io.github.genkidoudou.system.internal.entity.SysUserRole;
import io.github.genkidoudou.system.internal.mapper.SysRoleMapper;
import io.github.genkidoudou.system.internal.mapper.SysUserRoleMapper;
import io.github.genkidoudou.system.internal.service.ISysRoleAssignableService;
import io.github.genkidoudou.system.internal.service.ISysRoleMenuService;
import io.github.genkidoudou.system.internal.service.ISysRoleService;
import io.github.genkidoudou.system.internal.service.ISysUserRoleService;
import io.github.genkidoudou.system.internal.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现。
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends BaseVoServiceImpl<SysRoleMapper, SysRole, SysRoleVo> implements ISysRoleService {

    private final ISysRoleMenuService sysRoleMenuService;


    private final ISysRoleAssignableService sysRoleAssignableService;
    private final ISysUserRoleService iSysUserRoleService;

    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysRole> query, SysRoleVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getRoleName()), SysRole::getRoleName, param.getRoleName()).like(StrUtil.isNotBlank(param.getRoleCode()), SysRole::getRoleCode, param.getRoleCode()).eq(StrUtil.isNotBlank(param.getDataScope()), SysRole::getDataScope, param.getDataScope()).eq(StrUtil.isNotBlank(param.getStatus()), SysRole::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysRoleVo getVoById(Long id) {
        SysRoleVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "角色不存在");
        }
        // 查询绑定的角色id集合
        List<Long> menuIds = sysRoleMenuService.listByRoleId(id);
        vo.setMenuIds(menuIds);
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
    public SysRole saveVo(SysRoleVo vo) {

        // 校验code唯一
        long count = super.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, vo.getRoleCode()));
        if (count > 0) {
            throw new WarningException(10010, vo.getRoleCode());
        }
        SysRole sysRole = super.saveVo(vo);
        sysRoleMenuService.saveOrUpdate(sysRole.getId(), vo.getMenuIds());
        return sysRole;
    }

    /**
     * 修改；带事务。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysRoleVo vo) {
        // 校验code唯一
        long count = super.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, vo.getRoleCode()).ne(SysRole::getId, vo.getId()));
        if (count > 0) {
            throw new WarningException(10010, vo.getRoleCode());
        }
        sysRoleMenuService.saveOrUpdate(vo.getId(), vo.getMenuIds());
        return super.updateVoById(vo);
    }

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    @Override
    public void removeByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        // 判断如果该角色绑定用户 则不允许删除
        for (Long id : ids) {
            SysRole sysRole = super.getById(id);
            Long count = sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
            if (count > 0) {
                throw new WarningException(10011, sysRole.getRoleName());
            }
        }
        deleteByIds(ids);
        sysRoleMenuService.deleteByRoleIds(ids);


    }

    @Override
    public List<SysRoleVo> getAssignableByUserId(Long userId) {
        List<Long> roleIds = iSysUserRoleService.listRoleIdsByUserId(userId);
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        // 这里判断一下 有没有超级管理员的code, 如果有的话就加载全部的角色id
        List<SysRoleVo> sysRoleVoList = super.listVo(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds).eq(SysRole::getStatus, CommonEnums.STATUS_ENABLE.getValue()));
        boolean isAdmin = sysRoleVoList.stream().anyMatch(a -> a.getRoleCode().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return super.listVo(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, CommonEnums.STATUS_ENABLE.getValue()));
        } else {
            List<Long> assignableRoleIds = sysRoleAssignableService.listAssignableRoleIdsByRoleIds(roleIds);
            if (CollectionUtil.isEmpty(assignableRoleIds)) {
                return new ArrayList<>();
            }
            return super.listVo(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds).eq(SysRole::getStatus, CommonEnums.STATUS_ENABLE.getValue()));
        }
    }

    @Override
    public List<Long> listRoleIdByUserId(Long userId) {
        return iSysUserRoleService.listRoleIdsByUserId(userId);
    }

    @Override
    public void bindUserRole(Long userId, List<Long> roleIds) {
        // 删除绑定关系
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<SysUserRole> userRoleList = roleIds.stream().map(a -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(a);
                return sysUserRole;
            }).toList();
            sysUserRoleMapper.insert(userRoleList);

        }
    }

    @Override
    public void deleteBindRole(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));
    }

    @Override
    public List<SysRoleVo> listRoleByUserId(Long userId) {
        List<Long> roleIds = this.listRoleIdByUserId(userId);
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return super.listVo(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds).eq(SysRole::getStatus, CommonEnums.STATUS_ENABLE.getValue()));
    }
}
