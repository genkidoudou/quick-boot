package io.github.genkidoudou.common.datascope.annotation;

import java.lang.annotation.*;

/**
 * 数据权限
 *
 * @author luyanan
 * @since 2026/10/17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataPermission {


    /**
     * 参与控制的表名,支持通配（如 {@code order_*}
     *
     * @return
     * @since 2026/10/17
     */
    String[] tables();


    /**
     * 部门字段名。
     */
    String deptField() default "dept_id";

    /**
     * 用户字段名（「仅本人」等场景）；{@code sys_user} 应使用 {@code user_id}。
     */
    String userField() default "user_id";

    /**
     * 为 true 时跳过注入。
     */
    boolean ignore() default false;


}
