package io.github.genkidoudou.report.jimu;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 积木报表对外访问配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "qc.jimu")
public class JimuProperties {

    /**
     * 浏览器可访问的后端根地址（无尾斜杠），用于菜单 iframe，如 {@code http://localhost:9993}。
     */
    private String publicBaseUrl = "http://localhost:9993";
}
