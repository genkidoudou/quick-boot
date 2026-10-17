package io.github.genkidoudou.common.datascope;

import io.github.genkidoudou.common.datascope.annotation.DataPermission;

import java.util.Set;

public class DataScopeContext {

    private static final ThreadLocal<DataScope> HOLDER = new ThreadLocal<>();

    public static void set(DataScope scope) {
        HOLDER.set(scope);
    }

    public static DataScope get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 数据权限
     *
     * @param all            是否全部权限
     * @param deptIds        部门id
     * @param includeSelf    是否包含自己
     * @param userId         用户id
     * @param dataPermission 数据权限
     * @author luyanan
     * @since 2026/10/17
     */
    public record DataScope(boolean all, Set<Long> deptIds, boolean includeSelf, Long userId,
                            DataPermission dataPermission) {


    }
}
