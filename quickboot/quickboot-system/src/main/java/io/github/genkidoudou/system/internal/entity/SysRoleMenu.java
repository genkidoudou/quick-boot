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
 * 角色菜单关联实体，与表 {@code sys_role_menu} 对应。
 * <p>该表无逻辑删除与更新审计列，不继承 {@code BaseEntity}。
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

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

    /** 菜单 ID（列名 {@code menu_Id}）。 */
    @TableField("menu_Id")
    private Long menuId;
}
