package io.github.genkidoudou.web.event;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.system.api.api.SysLogininforApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysLogininforRecordVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 登录事件监听：写入 {@code sys_logininfor}。
 * <p>
 * 失败且未带 userId 时，按用户名补查；用户不存在则 userId 为空入库。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginEventListener {

    private final SysLogininforApi sysLogininforApi;

    private final SysUserApi sysUserApi;

    @EventListener
    public void onUserLogin(UserLoginEvent event) {
        if (event == null || !(event.getSource() instanceof UserLoginEventVo vo)) {
            return;
        }
        try {
            Long userId = vo.getUserId();
            if (userId == null && StrUtil.isNotBlank(vo.getUserName())) {
                SysUserVo user = sysUserApi.findByUserName(vo.getUserName());
                if (user != null) {
                    userId = user.getId();
                }
            }
            sysLogininforApi.record(SysLogininforRecordVo.builder()
                    .userId(userId)
                    .userName(vo.getUserName())
                    .clientId(vo.getClientId())
                    .ipAddr(vo.getIpAddr())
                    .browser(vo.getBrowser())
                    .loginLocation(vo.getLoginLocation())
                    .os(vo.getOs())
                    .status(vo.getStatus())
                    .loginTime(vo.getLoginTime() != null ? vo.getLoginTime() : LocalDateTime.now())
                    .traceId(vo.getTraceId())
                    .build());
        } catch (Exception e) {
            log.warn("record login log failed: {}", e.getMessage());
        }
    }
}
