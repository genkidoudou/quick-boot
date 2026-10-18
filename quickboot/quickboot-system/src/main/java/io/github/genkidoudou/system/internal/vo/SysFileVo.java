package io.github.genkidoudou.system.internal.vo;

import io.github.genkidoudou.common.file.url.FileUrl;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysFileVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "文件ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 分类编码。 */
    @NotBlank(message = "分类编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String classifyCode;

    /** 文件原始名。 */
    private String originalName;

    /** 文件路径。库内为相对路径，接口返回时拼成可访问地址。 */
    @FileUrl
    private String path;

    /** 文件后缀。 */
    private String ext;

    /** 文件大小（字节）。 */
    @Min(value = 0, message = "文件大小不能小于 0", groups = {AddGroup.class, UpdateGroup.class})
    private Long sizeBytes;

    /** 上传人显示名（昵称，缺省用户名）。仅接口返回，不入库。 */
    private String createName;
}
