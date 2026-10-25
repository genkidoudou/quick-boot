package io.github.genkidoudou.job.internal.quartz;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.UUID;

/**
 * 当前jvm的实例id
 *
 * @author luyanan
 * @since 2026/10/25
 */
@Component
public class JobInstanceId {
    private final String id;

    public JobInstanceId() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            host = "unknown";
        }
        // 例：app-host/12345@uuid短串 —— 重启后必变，旧日志自然无人续约
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        this.id = host + "/" + pid + "/" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String get() {
        return id;
    }
}