package io.github.genkidoudou.system.api.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典类型对外视图（跨模块查询用）。
 */
@Data
public class SysDictTypeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 字典名称。 */
    private String dictName;

    /** 字典类型编码。 */
    private String dictType;

    /** 状态(COMMON_STATUS)。 */
    private String status;

    /** 备注。 */
    private String remark;
}
