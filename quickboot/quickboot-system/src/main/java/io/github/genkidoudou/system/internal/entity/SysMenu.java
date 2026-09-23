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
 * 系统菜单实体，与表 {@code sys_menu} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_menu")
public class SysMenu extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 父 ID。 */
    private Long parentId;

    /** 菜单名称。 */
    private String menuName;

    /** 菜单类型(MENU_TYPE)。 */
    private String menuType;

    /** 路由地址。 */
    private String path;

    /** 路由名称。 */
    private String routeName;

    /** 组件地址。 */
    private String component;

    /** 权限标识。 */
    private String perms;

    /** 菜单图标。 */
    private String icon;

    /** 菜单排序。 */
    private Integer orderNum;

    /** 路由 query。 */
    private String query;

    /** 是否外链(0:否,1:是)。 */
    private String isFrame;

    /** 是否缓存(0:缓存, 1:不缓存)。 */
    private String isCache;

    /** 是否显示(0:显示,1:隐藏)。 */
    private String visible;

    /** 状态(COMMON_STATUS)。 */
    private String status;
}
