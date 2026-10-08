package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.excel.listener.ExcelResult;
import io.github.genkidoudou.system.internal.entity.SysDictType;
import io.github.genkidoudou.system.internal.vo.SysDictQuickVo;
import io.github.genkidoudou.system.internal.vo.SysDictTypeVo;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.util.List;

/**
 * 字典类型服务。
 */
public interface ISysDictTypeService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysDictTypeVo> pageVo(PageRequest<SysDictTypeVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysDictTypeVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysDictType saveVo(SysDictTypeVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysDictTypeVo vo);

    /**
     * 解析便捷文本为字典类型与字典项（不落库）。
     *
     * @param source 文本
     * @return 解析结果
     */
    SysDictQuickVo parseQuick(String source);

    /**
     * 一次性保存字典类型及其字典项。
     *
     * @param vo 类型 + 项
     */
    void quickSave(SysDictQuickVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 导出excel
     *
     * @param sysDictTypeVo
     * @param response
     * @return
     * @since 2026/10/1
     */
    void exportExcel(SysDictTypeVo sysDictTypeVo, HttpServletResponse response);

    /**
     * excel 导入
     *
     * @param inputStream   文件流
     * @param updateSupport 是否更新
     * @return
     * @since 2026/10/1
     */
    ExcelResult<SysDictTypeVo> importExcel(InputStream inputStream, boolean updateSupport);
}
