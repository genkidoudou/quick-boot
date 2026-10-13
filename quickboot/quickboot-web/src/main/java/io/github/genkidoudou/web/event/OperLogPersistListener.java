package io.github.genkidoudou.web.event;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.github.genkidoudou.common.monitor.operlog.OperLogCapturePayload;
import io.github.genkidoudou.common.monitor.operlog.OperLogCapturedEvent;
import io.github.genkidoudou.common.monitor.operlog.OperLogPublishingAspect;
import io.github.genkidoudou.common.monitor.operlog.OperLogSensitiveMasker;
import io.github.genkidoudou.system.api.api.SysOperLogApi;
import io.github.genkidoudou.system.api.vo.SysOperLogRecordVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 操作日志落库：监听 {@link OperLogCapturedEvent}，写入 {@code sys_oper_log}。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperLogPersistListener {

    private static final String STATUS_OK = "0";
    private static final String STATUS_FAIL = "1";

    private final SysOperLogApi sysOperLogApi;
    private final ObjectMapper objectMapper;

    @EventListener
    public void onOperLogCaptured(OperLogCapturedEvent event) {
        if (event == null || event.getPayload() == null) {
            return;
        }
        OperLogCapturePayload payload = event.getPayload();
        try {
            MethodSignature ms = payload.getSignature() instanceof MethodSignature signature
                    ? signature
                    : null;
            String methodName = resolveMethodName(ms);
            String params = "";
            if (ms != null) {
                params = OperLogPublishingAspect.serializeParams(ms, payload.getArgs(), objectMapper);
            }
            params = OperLogSensitiveMasker.mask(params);
            String jsonResult = OperLogSensitiveMasker.mask(
                    OperLogPublishingAspect.serializeResult(payload.getResult(), objectMapper));

            String browser = "";
            String os = "";
            if (StrUtil.isNotBlank(payload.getUserAgent())) {
                UserAgent agent = UserAgentUtil.parse(payload.getUserAgent());
                if (agent != null) {
                    browser = agent.getBrowser() != null ? agent.getBrowser().getName() : "";
                    os = agent.getOs() != null ? agent.getOs().getName() : "";
                }
            }

            String errorMsg = null;
            if (payload.getThrowable() != null) {
                errorMsg = StrUtil.sub(StrUtil.blankToDefault(
                        payload.getThrowable().getLocalizedMessage(),
                        payload.getThrowable().getClass().getSimpleName()), 0, 1024);
            }

            sysOperLogApi.record(SysOperLogRecordVo.builder()
                    .method(StrUtil.sub(methodName, 0, 512))
                    .requestMethod(payload.getRequestMethod())
                    .operName(payload.getLoginUsername())
                    .operUrl(StrUtil.sub(payload.getRequestUri(), 0, 255))
                    .clientId(payload.getClientId())
                    .ipAddr(payload.getRequestIp())
                    .browser(browser)
                    .os(os)
                    .params(StrUtil.sub(params, 0, 2048))
                    .jsonResult(jsonResult)
                    .status(payload.getThrowable() != null ? STATUS_FAIL : STATUS_OK)
                    .errorMsg(errorMsg)
                    .operTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(payload.getEndTimeMs()), ZoneId.systemDefault()))
                    .costTime(Math.max(0L, payload.getEndTimeMs() - payload.getStartTimeMs()))
                    .traceId(payload.getTraceId())
                    .build());
        } catch (Exception e) {
            log.warn("record oper log failed: {}", e.getMessage());
        }
    }

    private static String resolveMethodName(MethodSignature ms) {
        if (ms == null) {
            return "";
        }
        Method method = ms.getMethod();
        if (method == null || method.getDeclaringClass() == null) {
            return ms.toShortString();
        }
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
