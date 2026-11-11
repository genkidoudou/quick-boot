package io.github.genkidoudou.system.api.api;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.system.api.vo.SysDictTypeVo;

import java.util.List;

/**
 * 字典api
 *
 * @author luyanan
 * @since 2026/10/1
 */
public interface SysDictApi {
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

    /**
     * 字典类型分页。
     *
     * @param request 分页与筛选（dictName / dictType / status）
     * @return 分页结果
     */
    PageInfo<SysDictTypeVo> pageDictType(PageRequest<SysDictTypeVo> request);

}
