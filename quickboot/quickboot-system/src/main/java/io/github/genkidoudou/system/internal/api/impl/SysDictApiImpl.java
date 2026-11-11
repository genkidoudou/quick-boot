package io.github.genkidoudou.system.internal.api.impl;

import cn.hutool.core.bean.BeanUtil;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.system.api.api.SysDictApi;
import io.github.genkidoudou.system.api.vo.SysDictTypeVo;
import io.github.genkidoudou.system.internal.service.ISysDictDataService;
import io.github.genkidoudou.system.internal.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictApiImpl implements SysDictApi {

    private final ISysDictDataService sysDictDataService;

    private final ISysDictTypeService sysDictTypeService;

    /**
     * 根据字典type查询字典项
     *
     * @param dictType 字典type
     * @return
     * @since 2026/10/1
     */
    @Override
    public List<DictVo> listByDictType(String dictType) {
        return sysDictDataService.listByDictType(dictType)
                .stream().map(a -> {
                    DictVo dictVo = new DictVo();
                    dictVo.setDictType(dictType);
                    dictVo.setDictValue(a.getDictValue());
                    dictVo.setDictLabel(a.getDictLabel());
                    return dictVo;
                }).collect(Collectors.toList());
    }

    /**
     * 根据字典项的值查询字典
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return
     * @since 2026/10/1
     */
    @Override
    public DictVo getByDictValue(String dictType, String dictValue) {
        return listByDictType(dictType)
                .stream().filter(a -> a.getDictValue().equals(dictValue))
                .findFirst().orElse(null);
    }

    /**
     * 根据字典label查询
     *
     * @param dictType  字典类型
     * @param dictLabel 字典label
     * @return
     * @since 2026/10/1
     */
    @Override
    public DictVo getByDictLabel(String dictType, String dictLabel) {
        return listByDictType(dictType)
                .stream().filter(a -> a.getDictLabel().equals(dictLabel))
                .findFirst().orElse(null);
    }

    @Override
    public PageInfo<SysDictTypeVo> pageDictType(PageRequest<SysDictTypeVo> request) {
        PageRequest<io.github.genkidoudou.system.internal.vo.SysDictTypeVo> inner =
                new PageRequest<>(request.getCurrent(), request.getSize(), null);
        if (request.getParam() != null) {
            inner.setParam(BeanUtil.copyProperties(request.getParam(),
                    io.github.genkidoudou.system.internal.vo.SysDictTypeVo.class));
        }
        PageInfo<io.github.genkidoudou.system.internal.vo.SysDictTypeVo> page =
                sysDictTypeService.pageVo(inner);
        PageInfo<SysDictTypeVo> out = new PageInfo<>();
        out.setCurrent(page.getCurrent());
        out.setSize(page.getSize());
        out.setTotal(page.getTotal());
        out.setPages(page.getPages());
        List<io.github.genkidoudou.system.internal.vo.SysDictTypeVo> records = page.getRecords();
        out.setRecords(records == null
                ? Collections.emptyList()
                : BeanUtil.copyToList(records, SysDictTypeVo.class));
        return out;
    }
}
