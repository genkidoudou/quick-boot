package io.github.genkidoudou.common.datascope;

import io.github.genkidoudou.common.datascope.annotation.DataPermission;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * 拦截器
 *
 * @author luyanan
 * @since 2026/10/17
 */
@RequiredArgsConstructor
@Aspect
@Order(0)
public class DataPermissionAspect {

    private final DataScopeSupport dataScopeSupport;

    @Around("@annotation(dataPermission)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DataPermission dataPermission) throws Throwable {
        try {
            if (null != dataPermission && !dataPermission.ignore()) {
                DataScopeContext.DataScope dataScope = dataScopeSupport.getScope(LoginUserUtils.getLoginUser(), dataPermission);
                DataScopeContext.set(dataScope);
            }
            return proceedingJoinPoint.proceed();
        } finally {
            DataScopeContext.clear();
        }
    }
}
