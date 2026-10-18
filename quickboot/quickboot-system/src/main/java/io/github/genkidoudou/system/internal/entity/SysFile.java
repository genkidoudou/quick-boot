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
 * 文件实体，与表 {@code sys_file} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_file")
public class SysFile extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 分类编码。 */
    private String classifyCode;

    /** 文件原始名。 */
    private String originalName;

    /** 文件路径。 */
    private String path;

    /** 文件后缀。 */
    private String ext;

    /** 文件大小（字节）。 */
    private Long sizeBytes;
}
