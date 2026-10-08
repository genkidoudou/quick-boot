package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.excel.ExcelUtils;
import io.github.genkidoudou.common.excel.listener.ExcelResult;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysDictTypeService;
import io.github.genkidoudou.system.internal.vo.SysDictParseRequestVo;
import io.github.genkidoudou.system.internal.vo.SysDictQuickVo;
import io.github.genkidoudou.system.internal.vo.SysDictTypeVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/dictType")
public class SysDictTypeController {

    private final ISysDictTypeService sysDictTypeService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:dictType:list")
    @PostMapping("page")
    public R<PageInfo<SysDictTypeVo>> page(@RequestBody PageRequest<SysDictTypeVo> pageRequest) {
        return R.ok(sysDictTypeService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:dictType:query")
    @GetMapping("{id}")
    public R<SysDictTypeVo> get(@PathVariable Long id) {
        return R.ok(sysDictTypeService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:dictType:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysDictTypeVo body) {
        sysDictTypeService.saveVo(body);
        return R.ok();
    }

    /**
     * 解析便捷文本，不落库。
     *
     * @param body 文本
     * @return 字典类型与字典项
     */
    @SaCheckPermission("system:dictType:add")
    @PostMapping("parse")
    public R<SysDictQuickVo> parse(@RequestBody @Validated SysDictParseRequestVo body) {
        return R.ok(sysDictTypeService.parseQuick(body.getSource()));
    }

    /**
     * 便捷添加：一次保存字典类型及字典项。
     *
     * @param body 类型 + 项
     */
    @SaCheckPermission("system:dictType:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("quickAdd")
    public R<Void> quickAdd(@RequestBody SysDictQuickVo body) {
        sysDictTypeService.quickSave(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:dictType:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysDictTypeVo body) {
        sysDictTypeService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:dictType:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysDictTypeService.removeByIds(ids);
        return R.ok();
    }


    /**
     * 导出到excel
     *
     * @param response      文件流
     * @param sysDictTypeVo 查询参数
     * @return
     * @since 2026/10/1
     */
    @SaCheckPermission("system:dictType:export")
    @PostMapping("exportExcel")
    public void exportExcel(SysDictTypeVo sysDictTypeVo, HttpServletResponse response) {

        sysDictTypeService.exportExcel(sysDictTypeVo, response);
    }

    /**
     * 导入excel模板
     *
     * @author luyanan
     * @since 2026/10/1
     */
    @PostMapping("importExcelTemplate")
    @SaCheckPermission("system:dictType:import")
    public void importExcelTemplate(HttpServletResponse response) {
        ExcelUtils.exportImportTemplate( "字典类型", SysDictTypeVo.class, response);
    }

    /**
     * excel 导入
     *
     * @author luyanan
     * @since 2026/10/1
     */

    @SaCheckPermission("system:dictType:import")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("importExcel")
    public R<ExcelResult<SysDictTypeVo>> importExcel(@RequestParam("file") MultipartFile file, @RequestParam(value = "updateSupport", defaultValue = "false")
    boolean updateSupport) throws IOException {
        ExcelResult<SysDictTypeVo> excelResult = sysDictTypeService.importExcel(file.getInputStream(), updateSupport);
        return R.ok(excelResult);
    }


}
