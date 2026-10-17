package io.github.genkidoudou.system.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限，对应字典 {@code USER_DATA_SCOPE}。
 */
@Getter
@AllArgsConstructor
public enum UserDataScopeEnums {

    /** 全部数据权限。 */
    ALL("1", "全部数据权限"),

    /** 自定义数据权限。 */
    CUSTOM("2", "自定义数据权限"),

    /** 本部门数据权限。 */
    DEPT("3", "本部门数据权限"),

    /** 本部门及以下。 */
    DEPT_AND_CHILD("4", "本部门及以下"),

    /** 仅本人。 */
    SELF("5", "仅本人");

    /** 字典取值。 */
    private final String value;

    /** 字典标签。 */
    private final String label;

    /**
     * 按字典取值解析；无法识别时返回 {@code null}。
     *
     * @param value 字典取值
     * @return 对应枚举
     */
    public static UserDataScopeEnums of(String value) {
        if (value == null) {
            return null;
        }
        for (UserDataScopeEnums item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
