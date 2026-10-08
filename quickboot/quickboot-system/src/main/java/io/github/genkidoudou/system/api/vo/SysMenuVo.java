package io.github.genkidoudou.system.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @NotNull(message = "菜单ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 摘要。
     */
    private String digest;

    /**
     * 父 id。
     */
    @NotNull(message = "父菜单不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long parentId;

    /**
     * 菜单名称。
     */
    @NotBlank(message = "菜单名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String menuName;

    /**
     * 菜单类型(MENU_TYPE)。
     */
    @NotBlank(message = "菜单类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String menuType;

    /**
     * 路由地址。
     */
    private String path;

    /**
     * 路由名称。
     */
    private String routeName;

    /**
     * 组件地址。
     */
    private String component;

    /**
     * 权限标识。
     */
    private String perms;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 菜单排序。
     */
    private Integer orderNum;

    /**
     * 路由 query。
     */
    private String query;

    /**
     * 是否外链（0 否 1 是）。
     */
    private String isFrame;

    /**
     * 是否缓存（0 缓存 1 不缓存）。
     */
    private String isCache;

    /**
     * 是否显示（0 显示 1 隐藏）。
     */
    private String visible;

    /**
     * 状态(COMMON_STATUS)。
     */
    private String status;


    /**
     * 子菜单节点。
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SysMenuVo> children = new ArrayList<>();
}
