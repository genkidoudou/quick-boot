package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysRoleMenu;
import io.github.genkidoudou.system.internal.mapper.SysRoleMenuMapper;
import io.github.genkidoudou.system.internal.service.ISysRoleMenuService;
import io.github.genkidoudou.system.internal.vo.SysRoleVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色菜单绑定服务实现。
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    /**
     * 按角色主键查询菜单 id 列表。
     *
     * @param roleId 角色主键
     * @return 菜单 id 列表
     */
    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)).stream().map(SysRoleMenu::getMenuId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 覆盖角色与菜单的关联。
     *
     * @param roleId  角色主键
     * @param menuIds 菜单 id 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceMenuIds(Long roleId, List<Long> menuIds) {
        remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (CollectionUtil.isEmpty(menuIds)) {
            return;
        }
        List<SysRoleMenu> rows = menuIds.stream().filter(Objects::nonNull).distinct().map(mid -> {
            SysRoleMenu row = new SysRoleMenu();
            row.setRoleId(roleId);
            row.setMenuId(mid);
            return row;
        }).collect(Collectors.toList());
        saveBatch(rows);
    }

    @Override
    public void saveOrUpdate(Long roleId, List<Long> menuIds) {
        // 清空之前的数据
        this.baseMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<SysRoleMenu> roleMenuList = menuIds.stream().map(a -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(a);
                return sysRoleMenu;
            }).collect(Collectors.toList());
            super.saveBatch(roleMenuList);
        }
    }

    @Override
    public void deleteByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return;
        }
        super.baseMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
    }

    @Override
    public List<Long> listByRoleId(Long roleId) {
        return super.list(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());
    }
}
