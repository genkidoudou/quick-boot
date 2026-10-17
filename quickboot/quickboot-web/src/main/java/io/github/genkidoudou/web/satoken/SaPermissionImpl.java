package io.github.genkidoudou.web.satoken;

import cn.dev33.satoken.stp.StpInterface;
import io.github.genkidoudou.system.api.api.SysMenuApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 把 Sa-Token 的权限、角色查询接到菜单与角色数据上。
 */
@Component
@RequiredArgsConstructor
public class SaPermissionImpl implements StpInterface {

    private final SysMenuApi sysMenuApi;




    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return sysMenuApi.listPermsByUserId(Long.valueOf(loginId.toString()));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}
