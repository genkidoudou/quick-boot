package io.github.genkidoudou.system.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志写入参数（跨模块 API）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLogininforRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户 id；失败且用户不存在时可为空。 */
    private Long userId;

    /** 登录用户名。 */
    private String userName;

    /** 客户端 id。 */
    private String clientId;

    /** 登录 IP。 */
    private String ipAddr;

    /** 浏览器。 */
    private String browser;

    /** 登录地点。 */
    private String loginLocation;

    /** 操作系统。 */
    private String os;

    /** 登录状态(SYS_LOGIN_STATUS)：0 成功 / 1 失败。 */
    private String status;

    /** 登录时间。 */
    private LocalDateTime loginTime;

    /** 链路 id。 */
    private String traceId;
}
