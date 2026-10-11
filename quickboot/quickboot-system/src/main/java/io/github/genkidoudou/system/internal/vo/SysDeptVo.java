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
 * 部门视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "部门ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 上级部门。 */
    @NotNull(message = "上级部门不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long parentId;

    /** 部门名称。 */
    @NotBlank(message = "部门名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String deptName;

    /** 排序。 */
    private Integer orderNum;

    /** 负责人。 */
    private String leader;

    /** 联系电话。 */
    private String phone;

    /** 邮箱。 */
    private String email;

    /** 状态(COMMON_STATUS)。 */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String status;
}
