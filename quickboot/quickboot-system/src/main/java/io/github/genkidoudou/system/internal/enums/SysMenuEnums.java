package io.github.genkidoudou.system.internal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单枚举
 *
 * @author luyanan
 * @since 2026/10/8
 */
@Getter
@AllArgsConstructor
public enum SysMenuEnums {
    MENU_TYPE_DIR(TYPE.MENU_TYPE, "目录", "M"),
    MENU_TYPE_MENU(TYPE.MENU_TYPE, "菜单", "C"),
    MENU_TYPE_BUTTON(TYPE.MENU_TYPE, "按钮", "B"),

    ;

    /**
     * 类型
     *
     * @since 2026/10/8
     */

    private final TYPE type;

    /**
     * 字典值的名称
     *
     * @since 2026/10/8
     */

    private final String dictLabel;

    /**
     * 字典的值
     *
     * @since 2026/10/8
     */


    private final String dictValue;

    public static enum TYPE {
        //菜单类型
        MENU_TYPE;
    }
    }
