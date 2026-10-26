package io.github.genkidoudou.job.internal.demo;

import cn.hutool.core.util.RandomUtil;
import io.github.genkidoudou.job.internal.quartz.ITask;
import io.github.genkidoudou.job.internal.quartz.JobLogHelper;
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
        JobLogHelper.info("开始处理:参数为:{}", param);

        for (int i1 = 0; i1 < 100; i1++) {
            JobLogHelper.info("进度{}/100", i1);
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        JobLogHelper.info("处理结束:参数为:{}", param);
        return "处理完成，参数=" + param;
    }
}