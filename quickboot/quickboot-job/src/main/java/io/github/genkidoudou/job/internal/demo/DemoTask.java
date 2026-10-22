package io.github.genkidoudou.job.internal.demo;

import io.github.genkidoudou.job.internal.quartz.ITask;
import org.springframework.stereotype.Component;

/**
 * demo
 *
 * @author luyanan
 * @since 2026/10/22
 */
@Component("demoTask")
public class DemoTask implements ITask {
    @Override
    public String execute(String param) {
        return "处理完成，参数=" + param;
    }
}