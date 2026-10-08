package io.github.genkidoudou.common.excel;

import io.github.genkidoudou.common.common.DictVo;
import lombok.Data;

import java.util.List;

public interface DictSupport {


    /**
     * 根据字典type查询字典项
     *
     * @param dictType 字典type
     * @return
     * @since 2026/10/1
     */
    List<DictVo> listByDictType(String dictType);


    /**
     * 根据字典项的值查询字典
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return
     * @since 2026/10/1
     */
    DictVo getByDictValue(String dictType, String dictValue);


    /**
     * 根据字典label查询
     *
     * @param dictType  字典类型
     * @param dictLabel 字典label
     * @return
     * @since 2026/10/1
     */
    DictVo getByDictLabel(String dictType, String dictLabel);


}

