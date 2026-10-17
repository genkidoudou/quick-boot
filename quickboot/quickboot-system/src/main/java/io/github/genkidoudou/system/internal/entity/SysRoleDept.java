package io.github.genkidoudou.system.internal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色部门关联实体，与表 {@code sys_role_dept} 对应。
 * <p>关联表无完整审计/逻辑删除列时不继承 {@code BaseEntity}。
 */
@Data
@TableName("sys_role_dept")
public class SysRoleDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建人。 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 摘要。 */
    private String digest;

    /** 角色 ID。 */
    private Long roleId;

    /** 部门 ID。 */
    private Long deptId;
}
