package io.github.genkidoudou.common.mybatisplus;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Tier-1 标准 CRUD Service 模板：分页、详情等通用骨架（入参/出参均为 Vo）。
 * <p>
 * 子类实现 {@link #applyQuery}；复杂新增/修改/删除在子类 override。
 * Entity 转换在 {@link BaseServiceImpl#toVo}/{@link BaseServiceImpl#toEntity} 内完成。
 *
 * @param <M> Mapper 类型
 * @param <T> 实体类型
 * @param <V> Vo 类型
 */
public abstract class BaseVoServiceImpl<M extends BaseMapper<T>, T, V>
        extends BaseServiceImpl<M, T> implements CrudQuerySupport<T, V> {

    private Class<V> voClass;

    /**
     * 解析当前实现类上的 Vo 泛型。
     *
     * @return Vo Class
     */
    @SuppressWarnings("unchecked")
    protected Class<V> voClass() {
        if (voClass == null) {
            voClass = (Class<V>) GenericTypeUtils.resolveTypeArguments(getClass(), BaseVoServiceImpl.class)[2];
        }
        return voClass;
    }

    /**
     * 标准分页：从 {@link PageRequest#getParam()} 取查询 Vo 并委托 {@link #applyQuery}。
     *
     * @param pageRequest 分页与筛选
     * @return Vo 分页结果
     */
    public PageInfo<V> pageVo(PageRequest<V> pageRequest) {
        V param = pageRequest != null ? pageRequest.getParam() : null;
        return page(pageRequest, q -> applyQuery(q, param), voClass());
    }

    /**
     * 按主键查询 Vo。
     *
     * @param id 主键
     * @return Vo；不存在时 {@code null}
     */
    public V getVoById(Serializable id) {
        return super.getVoById(id, voClass());
    }

    /**
     * 按 Vo 新增并落库。
     *
     * @param vo 视图对象
     * @return 持久化后的实体；{@code vo} 为 null 时返回 null
     */
    public T saveVo(V vo) {
        if (vo == null) {
            return null;
        }
        T entity = toEntity(vo);
        this.baseMapper.insert(entity);
        return entity;
    }

    /**
     * 按 Vo 主键更新。
     *
     * @param vo 视图对象
     * @return 是否更新成功
     */
    public boolean updateVoById(V vo) {
        if (vo == null) {
            return false;
        }
        T entity = toEntity(vo);
        return this.baseMapper.updateById(entity) > 0;
    }

    /**
     * 按主键批量删除（逻辑删除或物理删除由全局配置决定）。
     *
     * @param ids 主键集合
     * @return 是否执行成功；空集合视为成功
     */
    public boolean deleteByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return true;
        }
        return this.baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 按条件查询单条并转为 Vo。
     *
     * @param lambdaQueryWrapper 条件
     * @return Vo；不存在时 {@code null}
     */
    public V getVoOne(LambdaQueryWrapper<T> lambdaQueryWrapper) {
        T entity = this.getOne(lambdaQueryWrapper, true);
        return toVo(entity, voClass());
    }
}
