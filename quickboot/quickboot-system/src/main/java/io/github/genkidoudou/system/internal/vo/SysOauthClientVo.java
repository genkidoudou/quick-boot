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
 * OAuth 客户端视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysOauthClientVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "客户端ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 客户端 id。 */
    @NotBlank(message = "客户端id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String clientId;

    /**
     * 客户端密钥。新增必填；修改时留空表示不改。
     */
    @NotBlank(message = "客户端密钥不能为空", groups = AddGroup.class)
    private String clientSecret;

    /** 客户端名称。 */
    @NotBlank(message = "客户端名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String clientName;

    /** 客户端类型：public|confidential。 */
    @NotBlank(message = "客户端类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String clientType;

    /** 允许的 grant_type，逗号分隔。 */
    @NotBlank(message = "授权类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String grantTypes;

    /** 允许访问的接口路径模式。 */
    @NotBlank(message = "接口路径不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String apiPathPatterns;

    /** token 有效时间（秒）。 */
    private Integer tokenTimeout;

    /** 是否校验验证码(COMMON_IS)。 */
    private String checkCaptcha;

    /** 状态(COMMON_STATUS)。 */
    private String status;
}
