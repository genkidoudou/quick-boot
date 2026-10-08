package io.github.genkidoudou.system.internal.vo;

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
 * 字典数据视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictDataVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "字典数据ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 字典类型。 */
    @NotBlank(message = "字典类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String dictType;

    /** 字典标签。 */
    @NotBlank(message = "字典标签不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String dictLabel;

    /** 字典值。 */
    @NotBlank(message = "字典值不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String dictValue;

    /** 排序。 */
    private Integer dictSort;

    /** css 样式类。 */
    private String cssClass;

    /** 回显样式。 */
    private String listClass;

    /** 是否默认(COMMON_IS)。 */
    private String isDefault;

    /** 状态(COMMON_STATUS)。 */
    private String status;
}
