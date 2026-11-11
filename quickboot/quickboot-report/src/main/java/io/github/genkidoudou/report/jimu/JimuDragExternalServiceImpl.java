package io.github.genkidoudou.report.jimu;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.common.security.utils.LoginUserUtils;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.system.api.api.SysDeptApi;
import io.github.genkidoudou.system.api.api.SysDictApi;
import io.github.genkidoudou.system.api.api.SysOperLogApi;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysDeptOptionVo;
import io.github.genkidoudou.system.api.vo.SysOperLogRecordVo;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.drag.service.IOnlDragExternalService;
import org.jeecg.modules.drag.vo.DragDictModel;
import org.jeecg.modules.drag.vo.DragLogDTO;
import org.jeecg.modules.jmreport.common.vo.JmDictModel;
import org.jeecg.modules.jmreport.desreport.service.IJimuReportDictService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JimuBI（大屏 / 仪表盘）外部扩展：系统字典 / 用户 / 部门 + 操作日志。
 * <p>
 * 表字典委托 {@link IJimuReportDictService}；普通字典走 {@link SysDictApi}。
 */
@Slf4j
@Component
public class JimuDragExternalServiceImpl implements IOnlDragExternalService {

    private final SysDictApi sysDictApi;

    private final SysUserApi sysUserApi;

    private final SysDeptApi sysDeptApi;

    private final SysOperLogApi sysOperLogApi;

    private final IJimuReportDictService reportDictService;

    public JimuDragExternalServiceImpl(
            SysDictApi sysDictApi,
            SysUserApi sysUserApi,
            SysDeptApi sysDeptApi,
            SysOperLogApi sysOperLogApi,
            @Lazy IJimuReportDictService reportDictService) {
        this.sysDictApi = sysDictApi;
        this.sysUserApi = sysUserApi;
        this.sysDeptApi = sysDeptApi;
        this.sysOperLogApi = sysOperLogApi;
        this.reportDictService = reportDictService;
    }

    @Override
    public Map<String, List<DragDictModel>> getManyDictItems(List<String> codeList, List<JSONObject> tableDictList) {
        Map<String, List<DragDictModel>> result = new HashMap<>();
        if (!CollectionUtils.isEmpty(codeList)) {
            for (String code : codeList) {
                if (StrUtil.isBlank(code)) {
                    continue;
                }
                result.put(code, getDictItems(code));
            }
        }
        if (!CollectionUtils.isEmpty(tableDictList)) {
            for (JSONObject item : tableDictList) {
                if (item == null) {
                    continue;
                }
                JSONObject object = JSONObject.parseObject(item.toString());
                String dictField = object.getString("dictField");
                String dictTable = object.getString("dictTable");
                String dictText = object.getString("dictText");
                String fieldName = object.getString("fieldName");
                if (StrUtil.isBlank(fieldName)) {
                    fieldName = dictTable + ":" + dictText + ":" + dictField;
                }
                result.put(fieldName, getTableDictItems(dictTable, dictText, dictField));
            }
        }
        return result;
    }

    @Override
    public List<DragDictModel> getDictItems(String dictCode) {
        List<DragDictModel> result = new ArrayList<>();
        if (StrUtil.isBlank(dictCode)) {
            return result;
        }
        List<DictVo> list = sysDictApi.listByDictType(dictCode);
        if (CollectionUtil.isEmpty(list)) {
            return result;
        }
        for (DictVo item : list) {
            result.add(toDragDict(item.getDictLabel(), item.getDictValue()));
        }
        return result;
    }

    @Override
    public List<DragDictModel> getTableDictItems(String dictTable, String dictText, String dictCode) {
        List<DragDictModel> result = new ArrayList<>();
        if (StrUtil.hasBlank(dictTable, dictText, dictCode)) {
            return result;
        }
        try {
            List<JmDictModel> items = reportDictService.queryTableDictItemsByCode(dictTable, dictText, dictCode);
            if (CollectionUtil.isEmpty(items)) {
                return result;
            }
            for (JmDictModel item : items) {
                DragDictModel model = new DragDictModel();
                BeanUtils.copyProperties(item, model);
                result.add(model);
            }
        } catch (Exception e) {
            log.warn("[jimubi] getTableDictItems failed, table={}, text={}, code={}, err={}",
                    dictTable, dictText, dictCode, e.getMessage());
        }
        return result;
    }

    @Override
    public List<DragDictModel> getCategoryTreeDictItems(List<String> ids) {
        // 系统暂无积木分类树模块
        return List.of();
    }

    @Override
    public List<DragDictModel> getUserDictItems(List<String> ids) {
        List<DragDictModel> result = new ArrayList<>();
        List<Long> userIds = parseLongIds(ids);
        if (userIds.isEmpty()) {
            return result;
        }
        List<SysUserVo> users = sysUserApi.listByUserIds(userIds);
        if (CollectionUtil.isEmpty(users)) {
            return result;
        }
        for (SysUserVo user : users) {
            if (user == null || user.getId() == null) {
                continue;
            }
            String text = StrUtil.blankToDefault(user.getNickName(), user.getUserName());
            result.add(toDragDict(text, String.valueOf(user.getId())));
        }
        return result;
    }

    @Override
    public List<DragDictModel> getDeptsDictItems(List<String> ids) {
        List<DragDictModel> result = new ArrayList<>();
        Set<Long> deptIds = new HashSet<>(parseLongIds(ids));
        if (deptIds.isEmpty()) {
            return result;
        }
        List<SysDeptOptionVo> depts = sysDeptApi.listAll();
        if (CollectionUtil.isEmpty(depts)) {
            return result;
        }
        for (SysDeptOptionVo dept : depts) {
            if (dept == null || dept.getId() == null || !deptIds.contains(dept.getId())) {
                continue;
            }
            result.add(toDragDict(dept.getDeptName(), String.valueOf(dept.getId())));
        }
        return result;
    }

    @Override
    public void addLog(DragLogDTO dto) {
        if (dto == null) {
            return;
        }
        log.info("[jimubi] drag log: {}", dto);
        recordOperLog("jimubi:drag", String.valueOf(dto));
    }

    @Override
    public void addLog(String logMsg, int logType, int operateType) {
        log.info("[jimubi] drag logMsg={}, logType={}, operateType={}", logMsg, logType, operateType);
        recordOperLog("jimubi:drag:" + operateType,
                "logType=" + logType + ", msg=" + StrUtil.blankToDefault(logMsg, ""));
    }

    private void recordOperLog(String method, String params) {
        try {
            LoginUser user = LoginUserUtils.getLoginUser();
            String operName = user != null
                    ? StrUtil.blankToDefault(user.getUsername(), "")
                    : "";
            sysOperLogApi.record(SysOperLogRecordVo.builder()
                    .method(StrUtil.sub(method, 0, 512))
                    .requestMethod("JIMUBI")
                    .operName(operName)
                    .operUrl("/jimubi")
                    .params(StrUtil.sub(params, 0, 2000))
                    .status("0")
                    .operTime(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.warn("[jimubi] write oper log failed: {}", e.getMessage());
        }
    }

    private static DragDictModel toDragDict(String text, String value) {
        DragDictModel model = new DragDictModel();
        model.setText(text);
        model.setValue(value);
        return model;
    }

    private static List<Long> parseLongIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return List.of();
        }
        return ids.stream()
                .filter(StrUtil::isNotBlank)
                .map(id -> {
                    try {
                        return Long.valueOf(id.trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
