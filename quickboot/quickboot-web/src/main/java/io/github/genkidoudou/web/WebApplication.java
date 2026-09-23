package io.github.genkidoudou.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主启动类
 *
 * @return
 * @since 2026/9/16
 */
@SpringBootApplication(scanBasePackages = {
        "io.github.genkidoudou.core",
        "io.github.genkidoudou.system",
        "io.github.genkidoudou.web"
})
@MapperScan({
   "io.github.genkidoudou.system.internal.mapper"
})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(WebApplication.class);
        application.run(args);
    }
}
