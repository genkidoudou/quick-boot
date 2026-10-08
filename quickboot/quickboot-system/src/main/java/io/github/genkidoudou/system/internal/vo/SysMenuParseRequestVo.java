package io.github.genkidoudou.system.internal.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 解析 Controller 源码请求。
 */
@Data
public class SysMenuParseRequestVo {

    /**
     * Controller 源码文本。
     */
    @NotBlank(message = "请粘贴 Controller 源码")
    private String source;
}
