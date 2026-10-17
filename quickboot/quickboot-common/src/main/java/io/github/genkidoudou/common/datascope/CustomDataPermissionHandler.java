package io.github.genkidoudou.common.datascope;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import io.github.genkidoudou.common.datascope.annotation.DataPermission;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomDataPermissionHandler implements MultiDataPermissionHandler {
    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {

        DataScopeContext.DataScope scope =
                DataScopeContext.get();
        if (null == scope || scope.all() || !matchTable(scope.dataPermission(), table.getName())) {
            return null;
        }
        DataPermission dataPermission = scope.dataPermission();
        String alias = table.getAlias() != null ? table.getAlias().getName() : table.getName();

        List<String> parts = new ArrayList<>();
        if (!scope.deptIds().isEmpty()) {
            String ids = scope.deptIds().stream().map(String::valueOf).collect(Collectors.joining(","));
            parts.add(alias + "." + dataPermission.deptField() + " IN (" + ids + ")");
        }
        if (scope.includeSelf() && scope.userId() != null) {
            parts.add(alias + "." + dataPermission.userField() + " = " + scope.userId());
        }
        String sql = parts.isEmpty() ? "1 = 0" : String.join(" OR ", parts);
        try {
            return CCJSqlParserUtil.parseCondExpression(sql);
        } catch (Exception ex) {
            throw new IllegalStateException("数据权限条件解析失败: " + sql, ex);
        }
    }

    /**
     * 表匹配
     *
     * @param rule      权限注解
     * @param tableName 表名
     * @return
     * @since 2026/10/17
     */
    private static boolean matchTable(DataPermission rule, String tableName) {
        if (tableName == null) {
            return false;
        }
        for (String pattern : rule.tables()) {
            if (pattern.endsWith("*")) {
                if (tableName.regionMatches(true, 0, pattern, 0, pattern.length() - 1)) {
                    return true;
                }
            } else if (pattern.equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }
}
