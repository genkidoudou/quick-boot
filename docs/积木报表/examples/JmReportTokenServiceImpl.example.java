package docs.jimureport.example;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.api.SysDictApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.jmreport.api.JmReportTokenServiceI;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 积木报表 Token / 角色 / 权限 / 字典 桥接示例。
 * <p>
 * 落地位置建议：
 * {@code quickboot-web/src/main/java/io/github/genkidoudou/web/jimureport/JmReportTokenServiceImpl.java}
 * <p>
 * 官方接口：{@code org.jeecg.modules.jmreport.api.JmReportTokenServiceI}
 * <p>
 * 对接要点：
 * <ol>
 *   <li>iframe（{@code IframeToggle}）会把 Admin-Token 拼到 {@code ?token=}</li>
 *   <li>Sa-Token 默认读 Header {@code Authorization: Bearer ...}</li>
 *   <li>因此 {@link #getToken} 必须兼容 query / header，必要时 {@code StpUtil.setTokenValue}</li>
 *   <li>首期：系统超管 → 积木 admin + 全套设计权限；普通用户只给预览所需最小权限</li>
 * </ol>
 * 方法签名以 2.5.6.1 的接口为准；若编译发现方法名/返回类型有差异，以 jar 内接口为准微调。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JmReportTokenServiceImplExample implements JmReportTokenServiceI {

    /** 系统字典：填报/报表下拉可复用 sys_dict_*。 */
    private final SysDictApi sysDictApi;

    /**
     * 从请求取出 token。
     * 优先级：Sa-Token 上下文 → 查询参数 token → 常见积木头。
     */
    @Override
    public String getToken(HttpServletRequest request) {
        String token = null;
        try {
            token = StpUtil.getTokenValue();
        } catch (Exception ignored) {
            // 上下文尚未写入时忽略
        }
        if (StrUtil.isBlank(token) && request != null) {
            token = request.getParameter("token");
            if (StrUtil.isBlank(token)) {
                token = request.getHeader("X-Access-Token");
            }
            if (StrUtil.isBlank(token)) {
                // 兼容 Authorization: Bearer xxx
                String auth = request.getHeader("Authorization");
                if (StrUtil.startWithIgnoreCase(auth, "Bearer ")) {
                    token = auth.substring(7).trim();
                }
            }
            if (StrUtil.isNotBlank(token)) {
                // 把 URL/头里的 token 灌进 Sa-Token，后续 verify / getUsername 才能用
                StpUtil.setTokenValue(token);
                log.debug("[jimu] init token from request, path={}", request.getRequestURI());
            }
        }
        return token;
    }

    /**
     * 校验 token 是否有效。失败则积木设计器/预览拒绝进入。
     */
    @Override
    public Boolean verifyToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            // 若当前上下文没有 token，先设再校验
            if (StrUtil.isBlank(StpUtil.getTokenValue())) {
                StpUtil.setTokenValue(token);
            }
            StpUtil.checkLogin();
            return true;
        } catch (Exception e) {
            log.warn("[jimu] verifyToken failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 返回登录名，用于积木操作人展示 / 审计。
     */
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

    /**
     * 积木内置角色：admin / lowdeveloper / dbadeveloper（以及部分版本的 chat2bi）。
     * 首期策略：系统角色含 admin（或超管）→ 给满；否则不给设计角色（仅预览）。
     */
    @Override
    public String[] getRoles(String token) {
        if (!isReportAdmin()) {
            return new String[]{};
        }
        return new String[]{"admin", "lowdeveloper", "dbadeveloper"};
    }

    /**
     * 积木按钮权限。设计器敏感操作（数据源、SQL 解析、删除等）都靠这些字符串。
     * 列表来自官方示例与 2.5.x 常见 @RequiresPermissions，落地后若缺权限再按 403 提示补。
     */
    @Override
    public String[] getPermissions(String token) {
        if (!isReportAdmin()) {
            // 普通用户：不给设计权限，只能看菜单挂好的预览页（具体预览权限按你们产品再收紧）
            return new String[]{};
        }
        return new String[]{
                // —— 报表 ——
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
                // —— BI / 仪表盘 ——
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
     * 把系统字典交给积木（填报/下拉）。
     * 返回结构以 2.5.6.1 接口为准；下列 Map 形态是常见约定（text/value）。
     * 若接口要求自定义 VO，请按 jar 签名改。
     */
    @Override
    public List<Map<String, Object>> getDictList(String dictCode) {
        List<DictVo> list = sysDictApi.listByDictType(dictCode);
        List<Map<String, Object>> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (DictVo item : list) {
            Map<String, Object> row = new HashMap<>(4);
            row.put("text", item.getDictLabel());
            row.put("value", item.getDictValue());
            row.put("label", item.getDictLabel());
            result.add(row);
        }
        return result;
    }

    /**
     * 是否报表管理员。首期：角色集合含 admin，或 permissions 含 {@code *:*:*}。
     * 落地后可改成独立权限字 {@code report:jimu:design}。
     */
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
}
