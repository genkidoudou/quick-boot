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
import java.util.List;

/**
 * 角色视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @NotNull(message = "角色ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 角色名称。
     */
    @NotBlank(message = "角色名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String roleName;

    /**
     * 角色编码。
     */
    @NotBlank(message = "角色编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String roleCode;

    /**
     * 角色排序。
     */
    private Integer roleSort;

    /**
     * 数据权限(USER_DATA_SCOPE)。
     */
    @NotBlank(message = "数据权限不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String dataScope;

    /**
     * 状态(COMMON_STATUS)。
     */
    @NotBlank(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String status;

    /**
     * 关联的菜单id集合
     *
     * @since 2026/10/10
     */

    private List<Long> menuIds;
}
