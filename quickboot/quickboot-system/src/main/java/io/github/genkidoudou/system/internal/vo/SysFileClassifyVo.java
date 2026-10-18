package io.github.genkidoudou.system.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件分类视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysFileClassifyVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "文件分类ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 分类编码。 */
    @NotBlank(message = "分类编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String classifyCode;

    /** 分类名称。 */
    @NotBlank(message = "分类名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String classifyName;

    /** 允许的文件后缀，逗号分隔、小写、不带点。 */
    @NotBlank(message = "允许的文件后缀不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String allowExt;

    /** 单个文件大小上限（字节）；为空表示不在本字段限制。 */
    @Min(value = 1, message = "单个文件大小上限必须大于 0", groups = {AddGroup.class, UpdateGroup.class})
    private Long maxSizeBytes;

    /** 每次可允许上传几个文件。 */
    @NotNull(message = "每次上传个数不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Min(value = 1, message = "每次上传个数必须大于 0", groups = {AddGroup.class, UpdateGroup.class})
    private Integer maxCount;

    /** 状态(COMMON_STATUS)。 */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String status;

    /** 是否可以匿名(COMMON_IS)。 */
    @NotBlank(message = "是否可以匿名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String anonymous;
}
