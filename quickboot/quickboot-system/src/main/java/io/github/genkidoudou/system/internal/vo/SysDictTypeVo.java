package io.github.genkidoudou.system.internal.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.genkidoudou.common.excel.annotation.ExcelDictFormat;
import io.github.genkidoudou.common.excel.conver.ExcelDictConvert;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典类型视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ExcelIgnoreUnannotated
public class SysDictTypeVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @NotNull(message = "字典类型ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 摘要。
     */
    private String digest;

    /**
     * 字典名称。
     */
    @ExcelProperty("字典名称")
    @NotBlank(message = "字典名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String dictName;

    /**
     * 字典类型。
     */
    @ExcelProperty("字典类型")
    @NotBlank(message = "字典类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String dictType;

    /**
     * 状态(COMMON_STATUS)。
     */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})

    @ExcelDictFormat(dictType = "COMMON_STATUS")
    @ExcelProperty(value = "字典状态", converter = ExcelDictConvert.class)
    private String status;
}
