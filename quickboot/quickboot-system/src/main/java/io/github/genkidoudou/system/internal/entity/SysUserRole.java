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
 * 用户角色关联实体，与表 {@code sys_user_role} 对应。
 * <p>该表无逻辑删除与更新审计列，不继承 {@code BaseEntity}。
 */
@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

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

    /** 用户 ID。 */
    private Long userId;

    /** 角色 ID（列名 {@code role_Id}）。 */
    @TableField("role_Id")
    private Long roleId;
}
