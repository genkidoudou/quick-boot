package io.github.genkidoudou.system.internal.vo;

import lombok.Data;

import java.util.List;

@Data
public class SysMenuSortRequestVo {

    /**
     * 菜单id集合
     *
     * @since 2026/10/4
     */

    private List<Long> menuIds;


    /**
     * 排序集合
     *
     * @since 2026/10/4
     */

    private List<Integer> orderNums;
}
