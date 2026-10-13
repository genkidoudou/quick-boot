package io.github.genkidoudou.system.internal.vo;

import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志视图对象。只读查询，不含新增/修改校验分组。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysLogininforVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    private Long id;

    /**
     * 登录的用户 id。
     */
    private Long userId;

    /**
     * 登录的用户名。
     */
    private String userName;

    /**
     * 登录的客户端 id。
     */
    private String clientId;

    /**
     * 登录的 ip。
     */
    private String ipAddr;

    /**
     * 浏览器类型。
     */
    private String browser;

    /**
     * 登录的地址。
     */
    private String loginLocation;

    /**
     * 操作系统。
     */
    private String os;

    /**
     * 登录状态(SYS_LOGIN_STATUS)。
     */
    private String status;

    /**
     * 登录时间。
     */
    private LocalDateTime loginTime;

    /**
     * 链路 id。
     */
    private String traceId;
}
