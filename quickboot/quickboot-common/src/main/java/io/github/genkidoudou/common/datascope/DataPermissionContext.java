package io.github.genkidoudou.common.datascope;

import io.github.genkidoudou.common.datascope.annotation.DataPermission;

import java.util.Set;

/**
 *
 * DataPermissionContext
 *
 * @author luyanan
 * @since 2026/10/17
 */
public final class DataPermissionContext {

    private static final ThreadLocal<DataPermission> HOLDER = new ThreadLocal<>();

    private DataPermissionContext() {

    }

    public static void set(DataPermission dataPermission) {
        HOLDER.set(dataPermission);
    }

    public static DataPermission get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }



}
