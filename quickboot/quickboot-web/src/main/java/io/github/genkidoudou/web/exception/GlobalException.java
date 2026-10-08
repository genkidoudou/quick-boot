package io.github.genkidoudou.web.exception;

import io.github.genkidoudou.common.api.HttpCodes;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.ErrorException;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.i18n.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    private static final String NOT_FOUND_MESSAGE = "访问资源不存在";


    /**
     * 兜底
     *
     * @author luyanan
     * @since 2026/9/19
     */
    @ExceptionHandler(ErrorException.class)
    public R errorException(ErrorException ex) {
        int code = ex.getCode();
        String message = I18nUtil.getMessage(code, ex.getArgs());
        return R.error(code, message);
    }


    /**
     * 兜底
     *
     * @author luyanan
     * @since 2026/9/19
     */
    @ExceptionHandler(WarningException.class)
    public R warningException(WarningException ex) {
        int code = ex.getCode();

        String message = I18nUtil.getMessage(code, ex.getArgs());
        return R.error(code, message);
    }

    /**
     * 未匹配到 Controller 或静态资源（Spring Boot 3 常见路径）
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public R handleNotFound(Exception ex) {
        log.warn("资源不存在: {}", ex.getMessage());
        return R.error(HttpCodes.NOT_FOUND, NOT_FOUND_MESSAGE);
    }

    /**
     * 兜底
     *
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
