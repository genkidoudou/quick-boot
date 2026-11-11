package io.github.genkidoudou.report.jimu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 积木目录项：供菜单管理下拉绑定预览地址。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JimuCatalogItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 积木侧主键（字符串，避免 JS 精度问题）。 */
    private String id;

    /** 显示名称。 */
    private String name;

    /** 相对预览 path，如 {@code /jmreport/view/{id}}。 */
    private String path;

    /** 绝对预览 URL（拼 {@code qc.jimu.public-base-url}），可直接写入菜单路由地址。 */
    private String url;
}
