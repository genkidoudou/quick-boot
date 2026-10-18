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
 * 文件分类实体，与表 {@code sys_file_classify} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_file_classify")
public class SysFileClassify extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 分类编码。 */
    private String classifyCode;

    /** 分类名称。 */
    private String classifyName;

    /** 允许的文件后缀。 */
    private String allowExt;

    /** 单个文件大小上限（字节）。 */
    private Long maxSizeBytes;

    /** 每次可允许上传几个文件。 */
    private Integer maxCount;

    /** 状态(COMMON_STATUS)。 */
    private String status;

    /** 是否可以匿名(COMMON_IS)。 */
    private String anonymous;
}
