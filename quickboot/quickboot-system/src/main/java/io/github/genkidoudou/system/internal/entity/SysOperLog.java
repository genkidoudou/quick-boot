package io.github.genkidoudou.system.internal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体，与表 {@code sys_oper_log} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 摘要。 */
    private String digest;

    /** 方法名。 */
    private String method;

    /** http 请求方式。 */
    private String requestMethod;

    /** 操作人。 */
    private String operName;

    /** 操作 url。 */
    private String operUrl;

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

    /** 参数。 */
    private String params;

    /** 返回结果。 */
    private String jsonResult;

    /** 状态(OPER_STATUS)。 */
    private String status;

    /** 错误信息。 */
    private String errorMsg;

    /** 操作时间。 */
    private LocalDateTime operTime;

    /** 耗时。 */
    private Long costTime;

    /** 链路 id。 */
    private String traceId;
}
