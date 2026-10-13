package io.github.genkidoudou.system.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志写入参数（跨模块 API）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysOperLogRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 方法名。 */
    private String method;

    /** HTTP 请求方式。 */
    private String requestMethod;

    /** 操作人。 */
    private String operName;

    /** 操作 URL。 */
    private String operUrl;

    /** 客户端 id。 */
    private String clientId;

    /** IP。 */
    private String ipAddr;

    /** 浏览器。 */
    private String browser;

    /** 登录地点。 */
    private String loginLocation;

    /** 操作系统。 */
    private String os;

    /** 请求参数。 */
    private String params;

    /** 返回结果。 */
    private String jsonResult;

    /** 状态(OPER_STATUS)：0 正常 / 1 异常。 */
    private String status;

    /** 错误信息。 */
    private String errorMsg;

    /** 操作时间。 */
    private LocalDateTime operTime;

    /** 耗时（毫秒）。 */
    private Long costTime;

    /** 链路 id。 */
    private String traceId;
}
