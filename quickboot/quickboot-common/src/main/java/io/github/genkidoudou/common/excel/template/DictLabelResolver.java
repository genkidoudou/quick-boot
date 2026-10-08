package io.github.genkidoudou.common.excel.template;

import cn.hutool.extra.spring.SpringUtil;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.common.excel.DictSupport;
import io.github.genkidoudou.common.excel.annotation.ExcelDictFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从 {@link ExcelDictFormat} 解析模板下拉用的标签列表。
 */
public final class DictLabelResolver {

  private static final Logger log = LoggerFactory.getLogger(DictLabelResolver.class);

  private DictLabelResolver() {
  }

  /**
   * 解析结果。
   *
   * @param labels          标签列表（可空）
   * @param skippedWithWarn 是否因字典服务未就绪等原因跳过下拉
   * @param warnMessage     跳过时的提示文案（可写入输入提示）
   */
  public record ResolveResult(List<String> labels, boolean skippedWithWarn, String warnMessage) {
    /**
     * @return 是否有可用下拉选项
     */
    public boolean hasLabels() {
      return labels != null && !labels.isEmpty();
    }
  }

  /**
   * 解析字段上的字典注解为 labels。
   *
   * @param format    注解；null 表示无字典
   * @param fieldName 字段名（日志）
   * @return 解析结果；无注解时 labels 为空且不 warn
   */
  public static ResolveResult resolve(ExcelDictFormat format, String fieldName) {
    if (format == null) {
      return new ResolveResult(List.of(), false, null);
    }
    String dictType = format.dictType();
    if (StringUtils.isNotBlank(dictType)) {
      DictSupport support;
      try {
        support = SpringUtil.getBean(DictSupport.class);
      } catch (Exception ex) {
        log.warn("字典服务未就绪，跳过模板下拉: field={}, dictType={}", fieldName, dictType);
        return new ResolveResult(List.of(), true, "字典未就绪，请按标签填写");
      }
      List<DictVo> items = support.listByDictType(dictType.trim());
      if (items == null || items.isEmpty()) {
        log.warn("字典无标签项，跳过模板下拉: field={}, dictType={}", fieldName, dictType);
        return new ResolveResult(List.of(), true, "字典无选项，请按标签填写");
      }
      List<String> labels = new ArrayList<>(items.size());
      for (DictVo item : items) {
        if (item != null && StringUtils.isNotBlank(item.getDictLabel())) {
          labels.add(item.getDictLabel());
        }
      }
      if (labels.isEmpty()) {
        return new ResolveResult(List.of(), true, "字典无选项，请按标签填写");
      }
      return new ResolveResult(List.copyOf(labels), false, null);
    }
    Map<String, String> valueToLabel = parseInline(format.dictText());
    if (valueToLabel.isEmpty()) {
      return new ResolveResult(List.of(), false, null);
    }
    return new ResolveResult(new ArrayList<>(valueToLabel.values()), false, null);
  }

  private static Map<String, String> parseInline(String[] dictText) {
    Map<String, String> map = new LinkedHashMap<>();
    if (dictText == null) {
      return map;
    }
    for (String item : dictText) {
      if (StringUtils.isBlank(item)) {
        continue;
      }
      int idx = item.indexOf('=');
      if (idx < 0) {
        continue;
      }
      map.put(item.substring(0, idx), item.substring(idx + 1));
    }
    return map;
  }
}
