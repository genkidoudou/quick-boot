package io.github.genkidoudou.common.api;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;

/**
 * 从 SLF4J {@link MDC} 读取链路 {@code traceId}，与 {@code logback} 中 {@code %X{traceId}} 约定一致。
 * <p>
 * 优先读 Micrometer Tracing 写入的 MDC；若当前线程尚无 traceId（例如部分过滤器链外、登录早期），
 * {@link #currentOrCreate()} 会生成并回写 MDC，保证登录/操作日志与 {@link R} 能带上同一链路号。
 * </p>
 */
public final class TraceIds {

  /**
   * 与现有日志 pattern 中 {@code traceId=%X{traceId}} 对齐的 MDC 键名。
   */
  public static final String MDC_KEY = "traceId";

  private TraceIds() {
  }

  /**
   * @return 非空白 traceId；否则 {@code null}
   */
  public static String current() {
    String v = MDC.get(MDC_KEY);
    if (StrUtil.isBlank(v)) {
      // 兼容部分 bridge 使用的键
      v = MDC.get("trace_id");
    }
    if (StrUtil.isBlank(v)) {
      return null;
    }
    return v.trim();
  }

  /**
   * 返回当前 traceId；若 MDC 中没有则生成并写入 MDC，供后续日志与响应共用。
   *
   * @return 非空 traceId
   */
  public static String currentOrCreate() {
    String existing = current();
    if (existing != null) {
      return existing;
    }
    String created = IdUtil.fastSimpleUUID();
    MDC.put(MDC_KEY, created);
    return created;
  }
}
