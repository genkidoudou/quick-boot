package io.github.genkidoudou.system.internal.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体，与表 {@code sys_logininfor} 对应。
 */
@Data
@TableName("sys_logininfor")
public class SysLogininfor  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 登录的用户 id。 */
    private Long userId;

    /** 登录的用户名。 */
    private String userName;

    /** 登录的客户端 id。 */
    private String clientId;

    /** 登录的 ip。 */
    private String ipAddr;

    /** 浏览器类型。 */
    private String browser;

    /** 登录的地址。 */
    private String loginLocation;

    /** 操作系统。 */
    private String os;

    /** 登录状态(SYS_LOGIN_STATUS)。 */
    private String status;

    /** 登录时间。 */
    private LocalDateTime loginTime;

    /** 链路 id。 */
    private String traceId;

    /**
     * 创建者用户 ID（字符串形式）。
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
