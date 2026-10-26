package io.github.genkidoudou.job.internal.quartz;

import lombok.Data;

/**
 * 按行拉取结果
 *
 * @author luyanan
 * @since 2026/10/26
 */
@Data
public class JobLogCatResult {


    private int fromLine;

    /**
     * 本次读到的最后一行,无新内容的时候  = formLine = 1
     *
     * @since 2026/10/26
     */

    private int toLine;


    /**
     * 内容
     *
     * @since 2026/10/26
     */

    private String content;


    /**
     * 是否是文件结尾
     *
     * @since 2026/10/26
     */

    private boolean end;


    /**
     * 文件不存在
     *
     * @since 2026/10/26
     */

    private boolean missing;
}
