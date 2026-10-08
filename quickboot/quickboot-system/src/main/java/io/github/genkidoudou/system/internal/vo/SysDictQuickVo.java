package io.github.genkidoudou.system.internal.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 便捷添加字典：类型 + 字典项。
 */
@Data
public class SysDictQuickVo {

    private String dictName;

    private String dictType;

    private String remark;

    private String status;

    private List<SysDictDataVo> items = new ArrayList<>();
}
