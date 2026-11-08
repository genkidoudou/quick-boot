package docs.jimureport.example;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.api.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 积木目录接口示例：供菜单管理绑定报表 / BI 页面。
 * <p>
 * 落地位置建议：
 * {@code quickboot-web/.../controller/JimuCatalogController.java}
 * 或独立 report 包。
 * <p>
 * 前端已存在：
 * <ul>
 *   <li>{@code GET /report/jimu/catalog/reports} → {@code listJimuReports()}</li>
 *   <li>{@code GET /report/jimu/catalog/bi-pages} → {@code listJimuBiPages()}</li>
 * </ul>
 * 见 {@code quick-ui/src/api/report/jimu.js}。
 * <p>
 * 表名以官方初始化 SQL 为准。下列 SQL 是常见结构示意；
 * <b>落地前请用 {@code SHOW TABLES LIKE '%jimu%'} / {@code DESC} 核对真实表名与字段</b>。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/report/jimu/catalog")
public class JimuCatalogControllerExample {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 报表目录。返回给菜单下拉：id / name / path。
     * path 建议直接给积木预览或列表地址，方便菜单 iframe 使用。
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/reports")
    public R<List<CatalogItem>> reports() {
        // 示意 SQL：请按 2.5.6.1 初始化后的真实表调整
        // 常见表名可能是 jimu_report / jimu_report_db 等
        String sql = """
                SELECT id, name
                FROM jimu_report
                WHERE del_flag = '0' OR del_flag IS NULL OR del_flag = 0
                ORDER BY update_time DESC, create_time DESC
                """;
        List<CatalogItem> items = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                CatalogItem item = new CatalogItem();
                item.setId(stringVal(row.get("id")));
                item.setName(stringVal(row.get("name")));
                // 预览地址示意：具体预览 path 以积木版本为准
                item.setPath("/jmreport/view/" + item.getId());
                items.add(item);
            }
        } catch (Exception ex) {
            // 表尚未创建或字段不一致时，返回空列表，避免菜单页直接 500
            return R.ok(List.of());
        }
        return R.ok(items);
    }

    /**
     * BI 大屏 / 仪表盘目录。
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/bi-pages")
    public R<List<CatalogItem>> biPages() {
        // 示意：onl_drag_page / jimu_drag_page 等，以官方 BI SQL 为准
        String sql = """
                SELECT id, name
                FROM onl_drag_page
                WHERE del_flag = '0' OR del_flag IS NULL OR del_flag = 0
                ORDER BY update_time DESC, create_time DESC
                """;
        List<CatalogItem> items = new ArrayList<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                CatalogItem item = new CatalogItem();
                item.setId(stringVal(row.get("id")));
                item.setName(stringVal(row.get("name")));
                item.setPath("/drag/share/view/" + item.getId());
                items.add(item);
            }
        } catch (Exception ex) {
            return R.ok(List.of());
        }
        return R.ok(items);
    }

    private static String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    @Data
    public static class CatalogItem {
        /** 积木侧主键（字符串，避免雪花精度问题）。 */
        private String id;
        /** 显示名称。 */
        private String name;
        /** 建议的菜单 path / iframe 地址。 */
        private String path;
    }
}
