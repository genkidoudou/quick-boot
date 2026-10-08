package io.github.genkidoudou.web.support;

import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.common.excel.DictSupport;
import io.github.genkidoudou.system.api.api.SysDictApi;
import io.github.genkidoudou.system.internal.service.ISysDictDataService;
import io.github.genkidoudou.system.internal.vo.SysDictDataVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictSupportImpl implements DictSupport {

    private final SysDictApi sysDictApi;


    /**
     * 根据字典type查询字典项
     *
     * @param dictType 字典type
     * @return
     * @since 2026/10/1
     */
    @Override
    public List<DictVo> listByDictType(String dictType) {


        return sysDictApi.listByDictType(dictType);
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
        return sysDictApi.getByDictValue(dictType, dictValue);
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
        return sysDictApi.getByDictLabel(dictType, dictLabel);
    }
}
