package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysDept;
import io.github.genkidoudou.system.internal.mapper.SysDeptMapper;
import io.github.genkidoudou.system.internal.service.ISysDeptService;
import io.github.genkidoudou.system.internal.vo.SysDeptVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 部门服务实现。
 */
@Service
public class SysDeptServiceImpl extends BaseVoServiceImpl<SysDeptMapper, SysDept, SysDeptVo>
        implements ISysDeptService {

    /** 树根上级部门 ID。 */
    public static final Long ROOT_PARENT_ID = 0L;

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysDept> query, SysDeptVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getDeptName()), SysDept::getDeptName, param.getDeptName())
                .like(StrUtil.isNotBlank(param.getLeader()), SysDept::getLeader, param.getLeader())
                .eq(param.getParentId() != null, SysDept::getParentId, param.getParentId())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysDept::getStatus, param.getStatus())
                .orderByAsc(SysDept::getOrderNum);
    }

    /**
     * 按条件查询部门，再用 Hutool {@link TreeUtil} 组装为树。
     * <p>{@code parentId} 作为树根，不参与 SQL 过滤，以便挂上整棵子树。
     *
     * @param sysDeptVo 条件
     * @return 树形部门
     */
    @Override
    public List<Tree<Long>> tree(SysDeptVo sysDeptVo) {
        SysDeptVo queryParam = sysDeptVo == null ? new SysDeptVo() : BeanUtil.copyProperties(sysDeptVo, SysDeptVo.class);
        Long rootId = queryParam.getParentId() == null ? ROOT_PARENT_ID : queryParam.getParentId();
        queryParam.setParentId(null);

        LambdaQueryWrapper<SysDept> query = new LambdaQueryWrapper<>();
        applyQuery(query, queryParam);
        query.orderByAsc(SysDept::getOrderNum);

        List<SysDeptVo> depts = listVo(query);
        if (CollectionUtil.isEmpty(depts)) {
            return new ArrayList<>();
        }
        depts = fillAncestors(depts);
        return buildTree(depts, rootId);
    }

    private static List<Tree<Long>> buildTree(List<SysDeptVo> depts, Long rootId) {
        return TreeUtil.build(depts, rootId, (dept, tree) -> {
            tree.setId(dept.getId());
            tree.setParentId(dept.getParentId() == null ? rootId : dept.getParentId());
            tree.setName(dept.getDeptName());
            tree.setWeight(dept.getOrderNum());
            tree.putExtra("deptId", dept.getId());
            tree.putExtra("parentId", dept.getParentId());
            tree.putExtra("deptName", dept.getDeptName());
            tree.putExtra("orderNum", dept.getOrderNum());
            tree.putExtra("leader", dept.getLeader());
            tree.putExtra("phone", dept.getPhone());
            tree.putExtra("email", dept.getEmail());
            tree.putExtra("status", dept.getStatus());
            tree.putExtra("remark", dept.getRemark());
        });
    }

    /**
     * 把命中节点的祖先一路补到根，便于搜索后仍按原层级展示。
     *
     * @param matched 条件命中的部门
     * @return 含祖先的节点集合
     */
    private List<SysDeptVo> fillAncestors(List<SysDeptVo> matched) {
        Map<Long, SysDeptVo> byId = new LinkedHashMap<>();
        for (SysDeptVo dept : matched) {
            if (dept.getId() != null) {
                byId.put(dept.getId(), dept);
            }
        }
        Set<Long> pending = new HashSet<>();
        for (SysDeptVo dept : matched) {
            Long pid = dept.getParentId();
            if (pid != null && !ROOT_PARENT_ID.equals(pid) && !byId.containsKey(pid)) {
                pending.add(pid);
            }
        }
        while (!pending.isEmpty()) {
            List<SysDept> parents = listByIds(pending);
            pending = new HashSet<>();
            if (CollectionUtil.isEmpty(parents)) {
                break;
            }
            for (SysDept entity : parents) {
                if (entity.getId() == null || byId.containsKey(entity.getId())) {
                    continue;
                }
                SysDeptVo vo = BeanUtil.copyProperties(entity, SysDeptVo.class);
                byId.put(vo.getId(), vo);
                Long pid = vo.getParentId();
                if (pid != null && !ROOT_PARENT_ID.equals(pid) && !byId.containsKey(pid)) {
                    pending.add(pid);
                }
            }
        }
        return new ArrayList<>(byId.values());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysDeptVo getVoById(Long id) {
        SysDeptVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "部门不存在");
        }
        return vo;
    }

    /**
     * 新增；带事务。根部门 parentId 默认为 0。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDept saveVo(SysDeptVo vo) {
        if (vo.getParentId() == null) {
            vo.setParentId(ROOT_PARENT_ID);
        }
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
    public boolean updateVoById(SysDeptVo vo) {
        if (vo.getParentId() == null) {
            vo.setParentId(ROOT_PARENT_ID);
        }
        if (vo.getId() != null && vo.getId().equals(vo.getParentId())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "上级部门不能是自己");
        }
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
