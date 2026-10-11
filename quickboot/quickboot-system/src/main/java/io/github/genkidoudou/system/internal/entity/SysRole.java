package io.github.genkidoudou.system.internal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色实体，与表 {@code sys_role} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role")
public class SysRole extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 角色名称。 */
    private String roleName;

    /** 角色编码。 */
    private String roleCode;

    /** 角色排序。 */
    private Integer roleSort;

    /** 数据权限(USER_DATA_SCOPE)。 */
    private String dataScope;

    /** 状态(COMMON_STATUS)。 */
    private String status;
}
