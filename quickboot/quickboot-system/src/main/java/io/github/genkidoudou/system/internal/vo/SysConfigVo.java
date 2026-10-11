package io.github.genkidoudou.system.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 参数配置视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysConfigVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "参数ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 参数键名。 */
    @NotBlank(message = "参数键名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String configKey;

    /** 参数名称。 */
    @NotBlank(message = "参数名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String configName;

    /** 参数值。 */
    @NotBlank(message = "参数值不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String configValue;

    /** 是否系统内置(COMMON_IS)。 */
    @NotBlank(message = "是否系统内置不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String configType;
}
