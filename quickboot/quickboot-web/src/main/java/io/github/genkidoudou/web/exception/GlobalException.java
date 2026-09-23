package io.github.genkidoudou.web.exception;

import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常
 *
 * @author luyanan
 * @since 2026/9/19
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalException {
    private static final String DEFAULT_FALLBACK_MESSAGE = "系统繁忙，请稍后再试";

    /**
     * 兜底
     * @author luyanan
     * @since 2026/9/19
     */
    @ExceptionHandler(Throwable.class)
    public R handleThrowable(Throwable ex) {
        ex.printStackTrace();
        int code = ErrorCodes.System.INTERNAL_ERROR;
        return R.error(code, DEFAULT_FALLBACK_MESSAGE);
    }

}
