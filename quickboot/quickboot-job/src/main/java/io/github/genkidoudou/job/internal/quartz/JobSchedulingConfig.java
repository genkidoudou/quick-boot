package io.github.genkidoudou.job.internal.quartz;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(JobLogProperties.class)
public class JobSchedulingConfig {
}