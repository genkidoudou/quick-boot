package io.github.genkidoudou.job.internal.demo;

import cn.hutool.core.util.RandomUtil;
import io.github.genkidoudou.job.internal.quartz.ITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * demo
 *
 * @author luyanan
 * @since 2026/10/22
 */
@Slf4j
@Component("demoTask")
public class DemoTask implements ITask {
    @Override
    public String execute(String param) {
        log.debug("开始处理:参数为:{}", param);
        int i = RandomUtil.randomInt(2);
        int res = 10 / i;
        try {
            Thread.sleep(3_000L*60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.debug("处理结束:参数为:{}", param);
        return "处理完成，参数=" + param;
    }
}