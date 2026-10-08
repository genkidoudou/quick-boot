package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.genkidoudou.common.excel.ExcelUtils;
import io.github.genkidoudou.common.excel.exception.ExcelDataCheckException;
import io.github.genkidoudou.common.excel.listener.ExcelResult;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysDictType;
import io.github.genkidoudou.system.internal.mapper.SysDictTypeMapper;
import io.github.genkidoudou.system.internal.service.ISysDictDataService;
import io.github.genkidoudou.system.internal.service.ISysDictTypeService;
import io.github.genkidoudou.system.internal.support.DictTextParser;
import io.github.genkidoudou.system.internal.vo.SysDictDataVo;
import io.github.genkidoudou.system.internal.vo.SysDictQuickVo;
import io.github.genkidoudou.system.internal.vo.SysDictTypeVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 字典类型服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends BaseVoServiceImpl<SysDictTypeMapper, SysDictType, SysDictTypeVo>
        implements ISysDictTypeService {

    private final ISysDictDataService sysDictDataService;

    /**
     * 按查询条件组装 Wrapper。
     * <p>字典名称、字典类型模糊匹配；状态精确匹配。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysDictType> query, SysDictTypeVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getDictName()), SysDictType::getDictName, param.getDictName())
                .like(StrUtil.isNotBlank(param.getDictType()), SysDictType::getDictType, param.getDictType())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysDictType::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysDictTypeVo getVoById(Long id) {
        SysDictTypeVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "字典类型不存在");
        }
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
    public SysDictType saveVo(SysDictTypeVo vo) {
        // 检查type是否唯一
        long count = this.count(new LambdaUpdateWrapper<SysDictType>()
                .eq(SysDictType::getDictType, vo.getDictType()));
        if (count > 0) {
            throw new WarningException(10001, vo.getDictType());
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
    public boolean updateVoById(SysDictTypeVo vo) {
        // 检查type是否唯一
        long count = this.count(new LambdaUpdateWrapper<SysDictType>()
                .eq(SysDictType::getDictType, vo.getDictType())
                .ne(SysDictType::getId, vo.getId()));
        if (count > 0) {
            throw new WarningException(10001, vo.getDictType());
        }
        return super.updateVoById(vo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SysDictQuickVo parseQuick(String source) {
        return DictTextParser.parse(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quickSave(SysDictQuickVo vo) {
        if (vo == null || StrUtil.isBlank(vo.getDictType()) || StrUtil.isBlank(vo.getDictName())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "字典名称和类型不能为空");
        }
        if (CollectionUtil.isEmpty(vo.getItems())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请至少添加一个字典项");
        }
        SysDictTypeVo typeVo = new SysDictTypeVo();
        typeVo.setDictName(vo.getDictName());
        typeVo.setDictType(vo.getDictType());
        typeVo.setRemark(vo.getRemark());
        typeVo.setStatus(StrUtil.blankToDefault(vo.getStatus(), "0"));
        saveVo(typeVo);
        List<SysDictDataVo> items = new ArrayList<>();
        int sort = 0;
        for (SysDictDataVo item : vo.getItems()) {
            if (item == null || StrUtil.isBlank(item.getDictLabel()) || StrUtil.isBlank(item.getDictValue())) {
                continue;
            }
            item.setDictType(vo.getDictType());
            if (item.getDictSort() == null) {
                item.setDictSort(sort);
            }
            if (StrUtil.isBlank(item.getIsDefault())) {
                item.setIsDefault(sort == 0 ? "1" : "0");
            }
            if (StrUtil.isBlank(item.getStatus())) {
                item.setStatus("0");
            }
            items.add(item);
            sort++;
        }
        if (items.isEmpty()) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请至少添加一个字典项");
        }
        sysDictDataService.saveBatchVo(items);
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

    @Override
    public void exportExcel(SysDictTypeVo sysDictTypeVo, HttpServletResponse response) {
        SysDictType sysDictType = BeanUtil.copyProperties(sysDictTypeVo, SysDictType.class);
        if (null == sysDictType) {
            sysDictType = new SysDictType();
        }
        List<SysDictTypeVo> sysDictTypeVos = this.listVo(new LambdaUpdateWrapper<SysDictType>(sysDictType));
        ExcelUtils.exportExcel(sysDictTypeVos, "字典类型", SysDictTypeVo.class, response);
    }

    @Override
    public ExcelResult<SysDictTypeVo> importExcel(InputStream inputStream, boolean updateSupport) {
        List<SysDictType> updateList = new ArrayList<>();
        List<SysDictType> saveList = new ArrayList<>();
        ExcelResult<SysDictTypeVo> excelResult = ExcelUtils.importExcel(inputStream, SysDictTypeVo.class, (sysDictTypeVo, analysisContext) -> {
            // 修改
            SysDictType type = this.baseMapper.selectOne(new LambdaUpdateWrapper<SysDictType>()
                    .eq(SysDictType::getDictType, sysDictTypeVo.getDictType()));
            if (null != type) {
                if (updateSupport) {
                    sysDictTypeVo.setId(type.getId());
                    updateList.add(BeanUtil.toBean(sysDictTypeVo, SysDictType.class));

                }else {
                    throw  new ExcelDataCheckException(sysDictTypeVo.getDictType()+"重复,请勿重复添加");
                }

            } else {
                saveList.add(BeanUtil.copyProperties(sysDictTypeVo, SysDictType.class));
            }
        }, (sysDictTypeVos, analysisContext) -> {

        });
        if (CollectionUtil.isNotEmpty(saveList)) {
            super.saveBatch(saveList);
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            super.updateBatchById(updateList);
        }

        return excelResult;

    }
}
