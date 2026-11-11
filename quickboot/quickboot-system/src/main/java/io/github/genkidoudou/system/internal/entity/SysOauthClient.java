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
 * OAuth 客户端实体，与表 {@code sys_oauth_client} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_oauth_client")
public class SysOauthClient extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 客户端 id。 */
    private String clientId;

    /** 客户端密钥。 */
    private String clientSecret;

    /** 客户端名称。 */
    private String clientName;

    /** 客户端类型：public|confidential。 */
    private String clientType;

    /** 允许的 grant_type，逗号分隔。 */
    private String grantTypes;

    /** 允许访问的接口路径模式。 */
    private String apiPathPatterns;

    /** token 有效时间（秒）。 */
    private Integer tokenTimeout;

    /** 是否校验验证码(COMMON_IS)。 */
    private String checkCaptcha;

    /** 状态(COMMON_STATUS)。 */
    private String status;
}
