package io.github.genkidoudou.system.internal.support;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.internal.vo.SysMenuBatchRequestVo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从 Controller 源码解析菜单页与按钮。
 */
public final class ControllerMenuParser {

    private static final Pattern CLASS_PATTERN = Pattern.compile("public\\s+class\\s+(\\w+)");
    private static final Pattern JAVADOC_PATTERN = Pattern.compile("/\\*\\*(.*?)\\*/", Pattern.DOTALL);
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile(
            "@RequestMapping\\(\\s*(?:value\\s*=\\s*)?[\"']([^\"']+)[\"']");
    private static final Pattern PERMISSION_PATTERN = Pattern.compile(
            "@SaCheckPermission\\(\\s*(?:value\\s*=\\s*)?[\"']([^\"']+)[\"']\\s*\\)");

    private static final Map<String, String> ACTION_LABEL = Map.of(
            "list", "列表",
            "query", "详情",
            "add", "新增",
            "edit", "修改",
            "remove", "删除",
            "export", "导出",
            "import", "导入"
    );

    private ControllerMenuParser() {
    }

    /**
     * @param source Controller 源码
     * @return 菜单 + 按钮
     */
    public static SysMenuBatchRequestVo parse(String source) {
        String text = StrUtil.trim(source);
        if (StrUtil.isBlank(text)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "请粘贴 Controller 源码");
        }
        Matcher classMatcher = CLASS_PATTERN.matcher(text);
        if (!classMatcher.find()) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "未识别到 Controller 类名");
        }
        String className = classMatcher.group(1);
        int classIndex = classMatcher.start();
        String head = text.substring(0, classIndex);
        String menuName = cleanMenuName(StrUtil.blankToDefault(lastJavadocSummary(head), className.replace("Controller", "")));
        String requestPath = lastAnnotationValue(head, REQUEST_MAPPING_PATTERN);
        RouteParts route = resolveRoute(requestPath, className);
        List<SysMenuVo> buttons = collectButtons(text.substring(classIndex), menuName);
        if (buttons.isEmpty()) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "未识别到 @SaCheckPermission");
        }
        String menuPerms = buttons.stream()
                .map(SysMenuVo::getPerms)
                .filter(item -> StrUtil.endWith(item, ":list"))
                .findFirst()
                .orElse(buttons.get(0).getPerms());

        SysMenuVo menu = new SysMenuVo();
        menu.setMenuName(menuName);
        menu.setMenuType("C");
        menu.setPath(route.path);
        menu.setComponent(route.component);
        menu.setRouteName(route.routeName);
        menu.setPerms(menuPerms);
        menu.setOrderNum(0);
        menu.setIsFrame("0");
        menu.setIsCache("0");
        menu.setVisible("0");
        menu.setStatus("0");

        SysMenuBatchRequestVo result = new SysMenuBatchRequestVo();
        result.setMenu(menu);
        result.setButtons(buttons);
        return result;
    }

    private static String cleanMenuName(String raw) {
        String name = StrUtil.trim(raw);
        name = name.replaceAll("[。.\\s]+$", "");
        if (name.endsWith("管理接口")) {
            name = name.substring(0, name.length() - 2);
        } else if (name.endsWith("接口")) {
            name = name.substring(0, name.length() - 2);
        }
        return name;
    }

    private static String lastJavadocSummary(String block) {
        Matcher matcher = JAVADOC_PATTERN.matcher(StrUtil.nullToEmpty(block));
        String last = "";
        while (matcher.find()) {
            last = javadocSummary(matcher.group(1));
        }
        return last;
    }

    private static String javadocSummary(String body) {
        for (String raw : StrUtil.nullToEmpty(body).split("\\R")) {
            String line = raw.replaceFirst("^\\s*\\*\\s?", "").trim();
            if (StrUtil.isBlank(line) || line.startsWith("@") || "/".equals(line)) {
                continue;
            }
            return line;
        }
        return "";
    }

    private static String lastAnnotationValue(String block, Pattern pattern) {
        Matcher matcher = pattern.matcher(StrUtil.nullToEmpty(block));
        String last = "";
        while (matcher.find()) {
            last = matcher.group(1);
        }
        return last;
    }

    private static RouteParts resolveRoute(String requestPath, String className) {
        String trimmed = StrUtil.removePrefix(StrUtil.removeSuffix(StrUtil.nullToEmpty(requestPath), "/"), "/");
        String[] parts = StrUtil.isBlank(trimmed) ? new String[0] : trimmed.split("/");
        String resource = parts.length > 0 ? parts[parts.length - 1] : fallbackResource(className);
        String moduleSeg = parts.length > 0 && "sys".equals(parts[0]) ? "system" : (parts.length > 0 ? parts[0] : "system");
        List<String> rest = new ArrayList<>();
        int start = 0;
        if (parts.length > 0 && ("sys".equals(parts[0]) || moduleSeg.equals(parts[0]))) {
            start = 1;
        }
        for (int i = start; i < parts.length; i++) {
            rest.add(parts[i]);
        }
        String viewPath = rest.isEmpty() ? resource : String.join("/", rest);
        return new RouteParts(resource, moduleSeg + "/" + viewPath + "/index", className.replace("Controller", ""));
    }

    private static String fallbackResource(String className) {
        String name = className.replace("Controller", "").replaceFirst("^Sys", "");
        if (name.isEmpty()) {
            return "index";
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static List<SysMenuVo> collectButtons(String body, String menuName) {
        Matcher matcher = PERMISSION_PATTERN.matcher(body);
        LinkedHashMap<String, SysMenuVo> unique = new LinkedHashMap<>();
        while (matcher.find()) {
            String perms = StrUtil.trim(matcher.group(1));
            if (StrUtil.isBlank(perms) || unique.containsKey(perms)) {
                continue;
            }
            String summary = lastJavadocSummary(body.substring(0, matcher.start()));
            String suffix = StrUtil.subAfter(perms, ":", true);
            String fallback = ACTION_LABEL.getOrDefault(suffix, suffix);
            SysMenuVo button = new SysMenuVo();
            button.setMenuName(StrUtil.isNotBlank(summary) ? cleanMenuName(summary) : menuName + fallback);
            button.setMenuType("F");
            button.setPerms(perms);
            button.setOrderNum(unique.size() + 1);
            button.setIsFrame("0");
            button.setIsCache("0");
            button.setVisible("0");
            button.setStatus("0");
            unique.put(perms, button);
        }
        return new ArrayList<>(unique.values());
    }

    private record RouteParts(String path, String component, String routeName) {
    }
}
