package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.entity.SysDictData;
import io.github.genkidoudou.system.internal.vo.SysDictDataVo;

import java.util.List;

/**
 * 字典数据服务。
 */
public interface ISysDictDataService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysDictDataVo> pageVo(PageRequest<SysDictDataVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysDictDataVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysDictData saveVo(SysDictDataVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysDictDataVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 批量新增字典项。
     *
     * @param list 字典项
     */
    void saveBatchVo(List<SysDictDataVo> list);

    /**
     * 按字典类型查询启用中的字典项（供前端 useDict / 下拉使用）。
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表，按排序升序
     */
    List<SysDictDataVo> listByDictType(String dictType);


}
