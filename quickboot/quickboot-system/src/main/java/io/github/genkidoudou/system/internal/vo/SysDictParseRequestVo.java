package io.github.genkidoudou.system.internal.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 解析字典便捷文本请求。
 */
@Data
public class SysDictParseRequestVo {

    /**
     * 便捷文本。
     */
    @NotBlank(message = "请输入字典文本")
    private String source;
}
