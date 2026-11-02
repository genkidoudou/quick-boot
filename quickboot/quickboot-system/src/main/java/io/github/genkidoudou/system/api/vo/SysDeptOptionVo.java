package io.github.genkidoudou.system.api.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门简要信息，供其他模块组装部门树。
 */
@Data
public class SysDeptOptionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 部门 ID。 */
    private Long id;

    /** 上级部门 ID，根为 0。 */
    private Long parentId;

    /** 部门名称。 */
    private String deptName;

    /** 状态（COMMON_STATUS）。 */
    private String status;

    /** 排序。 */
    private Integer orderNum;
}
