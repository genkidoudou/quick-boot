package io.github.genkidoudou.report.jimu;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.api.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 积木目录：菜单管理绑定报表 / BI 预览地址。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@EnableConfigurationProperties(JimuProperties.class)
@RequestMapping("/report/jimu/catalog")
public class JimuCatalogController {

    private final JdbcTemplate jdbcTemplate;

    private final JimuProperties jimuProperties;

    /**
     * 报表目录。
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/reports")
    public R<List<JimuCatalogItemVo>> reports() {
        String sql = """
                SELECT id, name
                FROM jimu_report
                WHERE del_flag IS NULL OR del_flag = 0
                ORDER BY update_time DESC, create_time DESC
                """;
        return R.ok(queryCatalog(sql, "/jmreport/view/"));
    }

    /**
     * BI 大屏 / 仪表盘目录。
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/bi-pages")
    public R<List<JimuCatalogItemVo>> biPages() {
        String sql = """
                SELECT id, name
                FROM onl_drag_page
                WHERE del_flag IS NULL OR del_flag = 0
                ORDER BY update_time DESC, create_time DESC
                """;
        return R.ok(queryCatalog(sql, "/drag/share/view/"));
    }

    private List<JimuCatalogItemVo> queryCatalog(String sql, String pathPrefix) {
        List<JimuCatalogItemVo> items = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                String id = stringVal(row.get("id"));
                if (StrUtil.isBlank(id)) {
                    continue;
                }
                String name = StrUtil.blankToDefault(stringVal(row.get("name")), id);
                String path = pathPrefix + id;
                items.add(new JimuCatalogItemVo(id, name, path, toAbsoluteUrl(path)));
            }
        } catch (Exception ex) {
            log.warn("[jimu] catalog query failed: {}", ex.getMessage());
            return List.of();
        }
        return items;
    }

    private String toAbsoluteUrl(String path) {
        String base = StrUtil.removeSuffix(StrUtil.blankToDefault(jimuProperties.getPublicBaseUrl(), ""), "/");
        if (StrUtil.isBlank(base)) {
            return path;
        }
        return base + path;
    }

    private static String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
