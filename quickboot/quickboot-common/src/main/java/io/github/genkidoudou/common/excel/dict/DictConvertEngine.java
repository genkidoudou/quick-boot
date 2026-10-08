package io.github.genkidoudou.common.excel.dict;

import cn.hutool.extra.spring.SpringUtil;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.common.excel.DictSupport;
import io.github.genkidoudou.common.excel.annotation.ExcelDictFormat;
import io.github.genkidoudou.common.excel.exception.ExcelDataCheckException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Excel 字典双向转换引擎（导出 value→label，导入 label→value）。
 */
public final class DictConvertEngine {

  private DictConvertEngine() {
  }

  /**
   * 导出：字典键值 → 标签。
   *
   * @param raw       字段原值
   * @param format    注解
   * @param fieldName 字段名（错误信息）
   * @return 转换后字符串；blank 原样
   */
  public static String toLabels(String raw, ExcelDictFormat format, String fieldName) {
    if (StringUtils.isBlank(raw) || format == null) {
      return raw;
    }
    String dictType = StringUtils.trimToEmpty(format.dictType());
    if (StringUtils.isNotEmpty(dictType)) {
      DictSupport support = resolveDictSupport(format, fieldName, dictType);
      if (support == null) {
        return applyMiss(raw, format, fieldName, dictType, false);
      }
      return convert(raw, format, fieldName, dictType, token -> {
        DictVo vo = support.getByDictValue(dictType, token);
        return vo == null ? null : vo.getDictLabel();
      });
    }
    Map<String, String> valueToLabel = parseInline(format.dictText());
    return convert(raw, format, fieldName, "", valueToLabel::get);
  }

  /**
   * 导入：标签 → 字典键值（混填：先按 label，再认合法 value）。
   *
   * @param raw       单元格原文
   * @param format    注解
   * @param fieldName 字段名（错误信息）
   * @return 转换后字符串；blank 原样
   */
  public static String toValues(String raw, ExcelDictFormat format, String fieldName) {
    if (StringUtils.isBlank(raw) || format == null) {
      return raw;
    }
    String dictType = StringUtils.trimToEmpty(format.dictType());
    if (StringUtils.isNotEmpty(dictType)) {
      DictSupport support = resolveDictSupport(format, fieldName, dictType);
      if (support == null) {
        return applyMiss(raw, format, fieldName, dictType, false);
      }
      return convert(raw, format, fieldName, dictType, token -> {
        DictVo byLabel = support.getByDictLabel(dictType, token);
        if (byLabel != null) {
          return byLabel.getDictValue();
        }
        DictVo byValue = support.getByDictValue(dictType, token);
        return byValue == null ? null : token;
      });
    }
    Map<String, String> valueToLabel = parseInline(format.dictText());
    Map<String, String> labelToValue = invert(valueToLabel);
    return convert(raw, format, fieldName, "", token -> {
      String byLabel = labelToValue.get(token);
      if (byLabel != null) {
        return byLabel;
      }
      return valueToLabel.containsKey(token) ? token : null;
    });
  }

  private static DictSupport resolveDictSupport(ExcelDictFormat format, String fieldName, String dictType) {
    try {
      return SpringUtil.getBean(DictSupport.class);
    } catch (Exception ex) {
      if (format.missPolicy() == DictMissPolicy.ERROR) {
        throw new ExcelDataCheckException(
          "字典服务未就绪: field=" + fieldName + ", dictType=" + dictType);
      }
      return null;
    }
  }

  private static String convert(String raw,
                                ExcelDictFormat format,
                                String fieldName,
                                String dictType,
                                Function<String, String> mapper) {
    String separator = format.separator();
    if (StringUtils.isEmpty(separator)) {
      return mapOne(raw, format, fieldName, dictType, mapper, false);
    }
    String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(raw, separator);
    List<String> out = new ArrayList<>(parts.length);
    for (String part : parts) {
      String mapped = mapOne(part, format, fieldName, dictType, mapper, true);
      if (mapped != null) {
        out.add(mapped);
      }
    }
    return String.join(separator, out);
  }

  private static String mapOne(String token,
                               ExcelDictFormat format,
                               String fieldName,
                               String dictType,
                               Function<String, String> mapper,
                               boolean multi) {
    String mapped = mapper.apply(token);
    if (mapped != null) {
      return mapped;
    }
    return applyMiss(token, format, fieldName, dictType, multi);
  }

  /**
   * @param multi 多值时 EMPTY 返回 null（跳过该段）；单值 EMPTY 返回空串
   */
  private static String applyMiss(String token,
                                  ExcelDictFormat format,
                                  String fieldName,
                                  String dictType,
                                  boolean multi) {
    DictMissPolicy policy = format.missPolicy() == null ? DictMissPolicy.KEEP : format.missPolicy();
    return switch (policy) {
      case KEEP -> token;
      case EMPTY -> multi ? null : "";
      case ERROR -> {
        String typePart = StringUtils.isNotBlank(dictType) ? ", dictType=" + dictType : "";
        throw new ExcelDataCheckException(
          "字典项未匹配: field=" + fieldName + typePart + ", token=" + token);
      }
    };
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

  private static Map<String, String> invert(Map<String, String> valueToLabel) {
    Map<String, String> labelToValue = new LinkedHashMap<>();
    for (Map.Entry<String, String> e : valueToLabel.entrySet()) {
      labelToValue.put(e.getValue(), e.getKey());
    }
    return labelToValue;
  }
}
