package io.github.genkidoudou.system.internal.api.impl;

import io.github.genkidoudou.system.api.api.SysDeptApi;
import io.github.genkidoudou.system.api.vo.SysDeptOptionVo;
import io.github.genkidoudou.system.internal.service.ISysDeptService;
import io.github.genkidoudou.system.internal.vo.SysDeptVo;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDeptApiImpl implements SysDeptApi {

    private final ISysDeptService sysDeptService;

    @Override
    public List<Long> listSelfAndChildIds(Long deptId) {
        return sysDeptService.listSelfAndChildIds(deptId);
    }

    @Override
    public List<Long> listByRoleId(List<Long> roleIds) {
        return sysDeptService.listByRoleId(roleIds);
    }

    @Override
    public List<SysDeptOptionVo> listAll() {
        return sysDeptService.listAll().stream().map(SysDeptApiImpl::toOption).toList();
    }

    private static SysDeptOptionVo toOption(SysDeptVo dept) {
        SysDeptOptionVo option = new SysDeptOptionVo();
        option.setId(dept.getId());
        option.setParentId(dept.getParentId());
        option.setDeptName(dept.getDeptName());
        option.setStatus(dept.getStatus());
        option.setOrderNum(dept.getOrderNum());
        return option;
    }
}
