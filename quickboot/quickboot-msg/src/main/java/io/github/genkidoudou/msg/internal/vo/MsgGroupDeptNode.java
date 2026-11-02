package io.github.genkidoudou.msg.internal.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 群组可选部门节点。已在组内的部门 {@code chosen=true}。
 */
@Data
public class MsgGroupDeptNode implements Serializable {

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

    /** 是否已在当前群组。 */
    private boolean chosen;
}
