package io.github.genkidoudou.system.internal.support;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.system.internal.vo.SysDictDataVo;
import io.github.genkidoudou.system.internal.vo.SysDictQuickVo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从便捷文本解析字典类型与字典项。
 * <pre>
 * 通用状态 COMMON_STATUS
 * 正常=0
 * 停用=1
 *
 * 或：MENU_TYPE 菜单类型
 * 目录=M, 菜单=C, 按钮=F
 *
 * 或：通用状态(COMMON_STATUS)
 * 正常:0
 * 停用:1
 * </pre>
 */
public final class DictTextParser {

    private static final Pattern HEADER_PAREN = Pattern.compile("^(.+?)\\s*\\(([A-Za-z][A-Za-z0-9_]*)\\)\\s*$");
    private static final Pattern HEADER_TYPE_FIRST = Pattern.compile("^([A-Za-z][A-Za-z0-9_]*)\\s+(.+)$");
    private static final Pattern HEADER_NAME_FIRST = Pattern.compile("^(.+?)\\s+([A-Za-z][A-Za-z0-9_]*)$");
    private static final Pattern TYPE_ONLY = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");
    private static final Pattern KV = Pattern.compile("^(.+?)\\s*[=:：]\\s*(.+)$");

    private DictTextParser() {
    }

    /**
     * @param source 文本
     * @return 类型 + 项
     */
    public static SysDictQuickVo parse(String source) {
        List<String> lines = new ArrayList<>();
        for (String raw : StrUtil.nullToEmpty(source).split("\\R")) {
            String line = StrUtil.trim(raw);
            if (StrUtil.isBlank(line) || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }
            lines.add(line);
        }
        if (lines.isEmpty()) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请输入字典文本");
        }
        Header header = parseHeader(lines.get(0));
        List<String> itemTokens = new ArrayList<>();
        if (StrUtil.isNotBlank(header.rest)) {
            splitItems(header.rest, itemTokens);
        }
        for (int i = 1; i < lines.size(); i++) {
            splitItems(lines.get(i), itemTokens);
        }
        if (itemTokens.isEmpty()) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "未识别到字典项，请按 标签=值 书写");
        }
        LinkedHashSet<String> values = new LinkedHashSet<>();
        List<SysDictDataVo> items = new ArrayList<>();
        int sort = 0;
        for (String token : itemTokens) {
            SysDictDataVo item = parseItem(token, sort);
            if (item == null || !values.add(item.getDictValue())) {
                continue;
            }
            item.setDictType(header.dictType);
            item.setDictSort(sort);
            item.setIsDefault(sort == 0 ? "1" : "0");
            item.setStatus("0");
            item.setListClass("default");
            items.add(item);
            sort++;
        }
        if (items.isEmpty()) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "未识别到有效字典项");
        }
        SysDictQuickVo result = new SysDictQuickVo();
        result.setDictName(header.dictName);
        result.setDictType(header.dictType);
        result.setStatus("0");
        result.setItems(items);
        return result;
    }

    private static Header parseHeader(String line) {
        Matcher paren = HEADER_PAREN.matcher(line);
        if (paren.matches()) {
            return new Header(StrUtil.trim(paren.group(1)), paren.group(2), null);
        }
        int colon = indexOfHeaderColon(line);
        String title = colon >= 0 ? StrUtil.trim(line.substring(0, colon)) : line;
        String rest = colon >= 0 ? StrUtil.trim(line.substring(colon + 1)) : null;
        Matcher typeFirst = HEADER_TYPE_FIRST.matcher(title);
        if (typeFirst.matches()) {
            return new Header(StrUtil.trim(typeFirst.group(2)), typeFirst.group(1), rest);
        }
        Matcher nameFirst = HEADER_NAME_FIRST.matcher(title);
        if (nameFirst.matches()) {
            return new Header(StrUtil.trim(nameFirst.group(1)), nameFirst.group(2), rest);
        }
        if (TYPE_ONLY.matcher(title).matches()) {
            return new Header(title, title, rest);
        }
        throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "首行无法识别字典名称和类型，例如：通用状态 COMMON_STATUS");
    }

    private static int indexOfHeaderColon(String line) {
        int a = line.indexOf('：');
        int b = line.indexOf(':');
        if (a < 0) {
            return b;
        }
        if (b < 0) {
            return a;
        }
        return Math.min(a, b);
    }

    private static void splitItems(String line, List<String> out) {
        for (String part : line.split("[,，;；]+")) {
            String token = StrUtil.trim(part);
            if (StrUtil.isNotBlank(token)) {
                out.add(token);
            }
        }
    }

    private static SysDictDataVo parseItem(String token, int index) {
        Matcher kv = KV.matcher(token);
        String label;
        String value;
        if (kv.matches()) {
            label = StrUtil.trim(kv.group(1));
            value = StrUtil.trim(kv.group(2));
        } else {
            String[] parts = token.split("\\s+");
            if (parts.length >= 2) {
                value = parts[parts.length - 1];
                label = StrUtil.trim(token.substring(0, token.length() - value.length()));
            } else {
                label = token;
                value = String.valueOf(index);
            }
        }
        if (StrUtil.isBlank(label) || StrUtil.isBlank(value)) {
            return null;
        }
        SysDictDataVo item = new SysDictDataVo();
        item.setDictLabel(label);
        item.setDictValue(value);
        return item;
    }

    private record Header(String dictName, String dictType, String rest) {
    }
}
