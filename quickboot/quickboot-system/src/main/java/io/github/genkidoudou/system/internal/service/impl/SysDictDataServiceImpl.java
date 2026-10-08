package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.system.internal.entity.SysDictData;
import io.github.genkidoudou.system.internal.mapper.SysDictDataMapper;
import io.github.genkidoudou.system.internal.service.ISysDictDataService;
import io.github.genkidoudou.system.internal.vo.SysDictDataVo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 字典数据服务实现。
 */
@CacheConfig(cacheNames = "sys-dict#3600")
@Service
public class SysDictDataServiceImpl extends BaseVoServiceImpl<SysDictDataMapper, SysDictData, SysDictDataVo>
        implements ISysDictDataService {

    /**
     * 按查询条件组装 Wrapper。
     * <p>字典类型、标签、值模糊匹配；是否默认、状态精确匹配。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysDictData> query, SysDictDataVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getDictType()), SysDictData::getDictType, param.getDictType())
                .like(StrUtil.isNotBlank(param.getDictLabel()), SysDictData::getDictLabel, param.getDictLabel())
                .like(StrUtil.isNotBlank(param.getDictValue()), SysDictData::getDictValue, param.getDictValue())
                .eq(StrUtil.isNotBlank(param.getIsDefault()), SysDictData::getIsDefault, param.getIsDefault())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysDictData::getStatus, param.getStatus())
                .orderByAsc(SysDictData::getDictSort);
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysDictDataVo getVoById(Long id) {
        SysDictDataVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "字典数据不存在");
        }
        return vo;
    }

    /**
     * 新增；带事务。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    @CacheEvict(allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDictData saveVo(SysDictDataVo vo) {
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @CacheEvict(allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysDictDataVo vo) {
        return super.updateVoById(vo);
    }

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    @CacheEvict(allEntries = true)
    @Override
    public void removeByIds(List<Long> ids) {
        deleteByIds(ids);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchVo(List<SysDictDataVo> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<SysDictData> rows = BeanUtil.copyToList(list, SysDictData.class);
        saveBatch(rows);
    }

    /**
     * 按字典类型查询启用中的字典项。
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表，按排序升序
     */
    @Cacheable(key = "'type:'+#dictType")
    @Override
    public List<SysDictDataVo> listByDictType(String dictType) {
        if (StrUtil.isBlank(dictType)) {
            return Collections.emptyList();
        }
        List<SysDictData> rows = list(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, CommonEnums.STATUS_ENABLE.getValue())
                .orderByAsc(SysDictData::getDictSort));
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(rows, SysDictDataVo.class);
    }


}
