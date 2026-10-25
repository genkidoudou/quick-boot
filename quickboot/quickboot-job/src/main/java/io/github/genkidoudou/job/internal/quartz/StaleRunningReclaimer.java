package io.github.genkidoudou.job.internal.quartz;

import io.github.genkidoudou.job.internal.service.ISysJobLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 僵尸 RUNNING 收尸。
 * <p>
 * 任意存活节点执行都可以：更新条件是 status=RUNNING AND heartbeat_time < 阈值，
 * 多节点同时跑最多重复 update，结果幂等。
 * <p>
 * 需要在启动类或 Job 模块配置类上启用：@EnableScheduling
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StaleRunningReclaimer {
    /**
     * 租约超时：应 > 续约周期 * 2～3。
     * 例：续约 20s → 超时 90s。太短易误杀长停顿；太长 UI 上 RUNNING 挂得久。
     */
    private static final long LEASE_TIMEOUT_SEC = 90;
    private final ISysJobLogService sysJobLogService;

    @Scheduled(fixedDelayString = "30000") // 每 30s 扫一次
    public void reclaim() {
        LocalDateTime staleBefore = LocalDateTime.now().minusSeconds(LEASE_TIMEOUT_SEC);
        int n = sysJobLogService.reclaimStaleRunning(staleBefore);
        if (n > 0) {
            log.info("收尸僵尸任务日志 {} 条，阈值={}", n, staleBefore);
        }
    }
}