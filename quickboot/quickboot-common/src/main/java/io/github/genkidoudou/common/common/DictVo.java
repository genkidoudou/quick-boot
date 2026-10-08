package io.github.genkidoudou.common.common;

import lombok.Data;

@Data
public class DictVo {

    /**
    * type
    * @since 2026/10/1
    */
    
    private String dictType;
    /**
     * 字典的值
     *
     * @since 2026/10/1
     */

    private String dictValue;


    /**
     * 字典标签名
     *
     * @since 2026/10/1
     */


    private String dictLabel;
}