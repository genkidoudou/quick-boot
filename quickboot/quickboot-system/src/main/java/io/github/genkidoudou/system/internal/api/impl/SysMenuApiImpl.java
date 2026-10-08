package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.system.api.api.SysMenuApi;
import io.github.genkidoudou.system.api.vo.RouterVo;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.internal.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysMenuApiImpl implements SysMenuApi {

    private final ISysMenuService sysMenuService;


    /**
     * 根据角色id集合查询关联的菜单id
     *
     * @param roleIds 角色id集合
     * @return
     * @since 2026/9/30
     */
    @Override
    public List<SysMenuVo> listByRoleIds(List<Long> roleIds) {
        return sysMenuService.listByRoleIds(roleIds);
    }

    @Override
    public List<RouterVo> selectMenuTreeByUserId(Long userId) {
        return sysMenuService.selectMenuTreeByUserId(userId);
    }
}
