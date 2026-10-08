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
 * 字典数据实体，与表 {@code sys_dict_data} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 字典类型。 */
    private String dictType;

    /** 字典标签。 */
    private String dictLabel;

    /** 字典值。 */
    private String dictValue;

    /** 排序。 */
    private Integer dictSort;

    /** css 样式类。 */
    private String cssClass;

    /** 回显样式。 */
    private String listClass;

    /** 是否默认(COMMON_IS)。 */
    private String isDefault;

    /** 状态(COMMON_STATUS)。 */
    private String status;
}
