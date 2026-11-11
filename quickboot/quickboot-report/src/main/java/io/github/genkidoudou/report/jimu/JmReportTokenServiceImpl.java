package io.github.genkidoudou.report.jimu;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.api.SysDictApi;
import io.github.genkidoudou.system.api.api.SysRoleApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysDictTypeVo;
import io.github.genkidoudou.system.api.vo.SysRoleVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.jmreport.api.JmReportTokenServiceI;
import org.jeecg.modules.jmreport.common.vo.JmDictModel;
import org.jeecg.modules.jmreport.common.vo.JmRoleModel;
import org.jeecg.modules.jmreport.common.vo.JmSysDictModel;
import org.jeecg.modules.jmreport.common.vo.JmUserModel;
import org.jeecg.modules.jmreport.desreport.model.JmPage;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 积木报表 Token / 角色 / 权限 / 字典桥接。
 * <p>
 * iframe 会带 {@code ?token=}；Sa-Token 默认读 {@code Authorization: Bearer}。
 * 首期：系统超管映射为积木设计角色与权限；普通用户仅可预览。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JmReportTokenServiceImpl implements JmReportTokenServiceI {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SysDictApi sysDictApi;

    private final SysUserApi sysUserApi;

    private final SysRoleApi sysRoleApi;

    /**
     * 取 token。积木后续 AJAX 多用 {@code X-Access-Token}，优先读请求再回落 Sa 上下文。
     */
    @Override
    public String getToken(HttpServletRequest request) {
        String token = resolveTokenFromRequest(request);
        if (StrUtil.isBlank(token)) {
            try {
                token = StpUtil.getTokenValue();
            } catch (Exception ignored) {
                // 上下文未初始化时忽略
            }
        }
        token = stripBearerPrefix(token);
        if (StrUtil.isNotBlank(token)) {
            StpUtil.setTokenValue(token);
            log.debug("[jimu] init token from request, path={}",
                    request != null ? request.getRequestURI() : "-");
        }
        return token;
    }

    @Override
    public String getToken() {
        try {
            return stripBearerPrefix(StpUtil.getTokenValue());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean verifyToken(String token) {
        token = stripBearerPrefix(token);
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            // 积木传入的 token 为准，避免上下文残留错误值
            StpUtil.setTokenValue(token);
            StpUtil.checkLogin();
            return true;
        } catch (Exception e) {
            log.warn("[jimu] verifyToken failed: {}", e.getMessage());
            return false;
        }
    }

    /** query token → X-Access-Token → Authorization Bearer */
    private static String resolveTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String token = request.getParameter("token");
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("X-Access-Token");
        }
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("token");
        }
        if (StrUtil.isBlank(token)) {
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StrUtil.startWithIgnoreCase(auth, "Bearer ")) {
                token = auth.substring(7).trim();
            }
        }
        return token;
    }

    private static String stripBearerPrefix(String token) {
        if (StrUtil.isBlank(token)) {
            return token;
        }
        String t = token.trim();
        if (StrUtil.startWithIgnoreCase(t, "Bearer ")) {
            return t.substring(7).trim();
        }
        return t;
    }

    @Override
    public String getUsername(String token) {
        LoginUser user = LoginUserUtils.getLoginUser();
        if (user != null && StrUtil.isNotBlank(user.getUsername())) {
            return user.getUsername();
        }
        try {
            return StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        Map<String, Object> info = new HashMap<>(8);
        LoginUser user = LoginUserUtils.getLoginUser();
        String username = getUsername(token);
        info.put(SYS_USER_CODE, username == null ? "" : username);
        info.put(sysUserName, user != null && StrUtil.isNotBlank(user.getNickName())
                ? user.getNickName()
                : (username == null ? "" : username));
        info.put(SYS_ORG_CODE, user != null && user.getDeptId() != null
                ? String.valueOf(user.getDeptId())
                : "");
        info.put(SYS_DATE, LocalDate.now().format(DATE));
        info.put(SYS_DATE_TIME, LocalDateTime.now().format(DATE_TIME));
        return info;
    }

    @Override
    public String[] getRoles(String token) {
        if (!isReportAdmin()) {
            return new String[]{};
        }
        return new String[]{"admin", "lowdeveloper", "dbadeveloper"};
    }

    @Override
    public String[] getPermissions(String token) {
        if (!isReportAdmin()) {
            return new String[]{};
        }
        return new String[]{
                "jmreport:exportJob:plugin",
                "jmreport:exportJob:list",
                "jmreport:exportJob:save",
                "jmreport:exportJob:detail",
                "jmreport:exportJob:delete",
                "jmreport:exportJob:status",
                "jmreport:exportJob:run",
                "jmreport:map:add",
                "jmreport:map:delete",
                "jmreport:reportGroup:list",
                "jmreport:reportGroup:detail",
                "jmreport:reportGroup:save",
                "jmreport:reportGroup:delete",
                "jmreport:category:add",
                "jmreport:category:edit",
                "jmreport:category:delete",
                "jmreport:category:reduction",
                "jmreport:category:completelyDelete",
                "jmreport:category:batchMove",
                "jmreport:category:folderSort",
                "jmreport:design:loadTableData",
                "drag:datasource:testConnection",
                "drag:datasource:saveOrUpate",
                "drag:datasource:delete",
                "drag:datasource:deleteBatch",
                "drag:datasource:queryById",
                "drag:dataset:save",
                "drag:dataset:delete",
                "drag:analysis:sql",
                "drag:design:getTotalData",
                "drag:iconlib:config",
                "onl:drag:clear:recovery",
                "onl:drag:page:delete",
                "onl:drag:category:delete",
                "onl:drag:comp:add",
                "onl:drag:comp:edit",
                "onl:drag:comp:delete",
                "onl:drag:comp:deleteBatch",
                "onl:drag:getRawTableData"
        };
    }

    /**
     * 按字典类型取下拉项（sys_dict_data）。
     */
    @Override
    public List<JmDictModel> getDictItems(String dictCode) {
        List<JmDictModel> result = new ArrayList<>();
        if (StrUtil.isBlank(dictCode)) {
            return result;
        }
        List<DictVo> list = sysDictApi.listByDictType(dictCode);
        if (CollectionUtil.isEmpty(list)) {
            return result;
        }
        for (DictVo item : list) {
            JmDictModel model = new JmDictModel();
            model.setText(item.getDictLabel());
            model.setValue(item.getDictValue());
            model.setDictCode(dictCode);
            result.add(model);
        }
        return result;
    }

    /**
     * 字典类型分页，供积木选择系统字典。
     */
    @Override
    public JmPage<JmSysDictModel> getDictList(String token, String dictName, String dictCode,
                                              Integer pageNo, Integer pageSize) {
        int current = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;

        SysDictTypeVo param = new SysDictTypeVo();
        param.setStatus("0");
        if (StrUtil.isNotBlank(dictName)) {
            param.setDictName(dictName.trim());
        }
        if (StrUtil.isNotBlank(dictCode)) {
            param.setDictType(dictCode.trim());
        }
        PageInfo<SysDictTypeVo> page = sysDictApi.pageDictType(new PageRequest<>(current, size, param));
        List<JmSysDictModel> records = new ArrayList<>();
        if (page != null && CollectionUtil.isNotEmpty(page.getRecords())) {
            for (SysDictTypeVo item : page.getRecords()) {
                records.add(new JmSysDictModel(
                        item.getDictType(),
                        item.getDictName(),
                        StrUtil.blankToDefault(item.getRemark(), "")));
            }
        }
        long total = page == null ? 0L : page.getTotal();
        return toJmPage(current, size, (int) Math.min(total, Integer.MAX_VALUE), records);
    }

    /**
     * 角色分页。
     */
    @Override
    public JmPage<JmRoleModel> getRoleList(String token, String keyword, Integer pageNo, Integer pageSize) {
        int current = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;

        SysRoleVo param = new SysRoleVo();
        param.setStatus("0");
        if (StrUtil.isNotBlank(keyword)) {
            // 现有 applyQuery 为 AND；keyword 先按角色名模糊匹配
            param.setRoleName(keyword.trim());
        }
        PageInfo<SysRoleVo> page = sysRoleApi.page(new PageRequest<>(current, size, param));
        List<JmRoleModel> records = new ArrayList<>();
        if (page != null && CollectionUtil.isNotEmpty(page.getRecords())) {
            for (SysRoleVo role : page.getRecords()) {
                records.add(new JmRoleModel(role.getRoleCode(), role.getRoleName()));
            }
        }
        long total = page == null ? 0L : page.getTotal();
        return toJmPage(current, size, (int) Math.min(total, Integer.MAX_VALUE), records);
    }

    /**
     * 用户分页（复用 SysUserApi）。
     */
    @Override
    public JmPage<JmUserModel> getUserList(String token, String keyword, Integer pageNo, Integer pageSize) {
        int current = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;

        SysUserVo param = new SysUserVo();
        if (StrUtil.isNotBlank(keyword)) {
            param.setUserName(keyword.trim());
        }
        PageInfo<SysUserVo> page = sysUserApi.page(new PageRequest<>(current, size, param), null);
        List<JmUserModel> records = new ArrayList<>();
        if (page != null && CollectionUtil.isNotEmpty(page.getRecords())) {
            for (SysUserVo user : page.getRecords()) {
                JmUserModel model = new JmUserModel();
                model.setUserId(user.getId() == null ? null : String.valueOf(user.getId()));
                model.setUsername(StrUtil.blankToDefault(user.getNickName(), user.getUserName()));
                records.add(model);
            }
        }
        long total = page == null ? 0L : page.getTotal();
        return toJmPage(current, size, (int) Math.min(total, Integer.MAX_VALUE), records);
    }

    @Override
    public HttpHeaders customApiHeader() {
        HttpHeaders headers = new HttpHeaders();
        try {
            String token = StpUtil.getTokenValue();
            if (StrUtil.isNotBlank(token)) {
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                headers.set("X-Access-Token", token);
                headers.set("token", token);
            }
        } catch (Exception ignored) {
            // 未登录时不附加
        }
        return headers;
    }

    private boolean isReportAdmin() {
        LoginUser user = LoginUserUtils.getLoginUser();
        if (user == null) {
            return false;
        }
        Set<String> roles = user.getRoles();
        if (roles != null && (roles.contains("admin") || roles.contains("Admin"))) {
            return true;
        }
        Set<String> permissions = user.getPermissions();
        return permissions != null && permissions.contains("*:*:*");
    }

    private static <T> JmPage<T> toJmPage(int pageNo, int pageSize, int total, List<T> records) {
        JmPage<T> page = new JmPage<>();
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setPages(pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize);
        page.setRecords(records);
        return page;
    }
}
