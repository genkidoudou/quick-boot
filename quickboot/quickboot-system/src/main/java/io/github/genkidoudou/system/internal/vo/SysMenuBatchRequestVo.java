package io.github.genkidoudou.system.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次保存菜单页及其按钮。
 */
@Data
public class SysMenuBatchRequestVo {

    /**
     * 菜单（C）。
     */
    @Valid
    @NotNull(message = "菜单不能为空", groups = AddGroup.class)
    private SysMenuVo menu;

    /**
     * 按钮（F），可为空。
     */
    private List<SysMenuVo> buttons = new ArrayList<>();
}
