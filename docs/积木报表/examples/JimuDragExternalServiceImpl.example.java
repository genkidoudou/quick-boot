package docs.jimureport.example;

import com.alibaba.fastjson.JSONObject;
import io.github.genkidoudou.common.common.DictVo;
import io.github.genkidoudou.system.api.api.SysDictApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.drag.service.IOnlDragExternalService;
import org.jeecg.modules.drag.vo.DragDictModel;
import org.jeecg.modules.drag.vo.DragLogDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JimuBI（大屏 / 仪表盘）外部扩展示例：字典 + 操作日志。
 * <p>
 * 落地位置建议：
 * {@code quickboot-web/src/main/java/io/github/genkidoudou/web/jimureport/JimuDragExternalServiceImpl.java}
 * <p>
 * 官方接口：{@code org.jeecg.modules.drag.service.IOnlDragExternalService}
 * <p>
 * 说明：
 * <ul>
 *   <li>BI 的「能不能进设计器」主要仍由 {@code JmReportTokenServiceI#getRoles/getPermissions} 决定</li>
 *   <li>本接口负责字典翻译、多字典批量、以及设计器操作日志钩子</li>
 *   <li>接口带 default 方法；首期可只覆盖字典，其余走默认实现</li>
 * </ul>
 * 类名 / 包路径以 2.5.6.1 jar 为准；若 DragDictModel 字段不同，按实际 setter 调整。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JimuDragExternalServiceImplExample implements IOnlDragExternalService {

    private final SysDictApi sysDictApi;

    /**
     * 按字典编码取下拉项（对应系统 sys_dict_type / sys_dict_data）。
     */
    @Override
    public List<DragDictModel> getDictItems(String dictCode) {
        List<DictVo> source = sysDictApi.listByDictType(dictCode);
        List<DragDictModel> list = new ArrayList<>();
        if (source == null) {
            return list;
        }
        for (DictVo item : source) {
            DragDictModel model = new DragDictModel();
            // 常见字段：text / value；若编译报错请对照 DragDictModel 实际属性
            model.setText(item.getDictLabel());
            model.setValue(item.getDictValue());
            list.add(model);
        }
        return list;
    }

    /**
     * 批量字典。tableDictList 为「表字典」配置，首期可先忽略表字典，只处理 codeList。
     */
    @Override
    public Map<String, List<DragDictModel>> getManyDictItems(List<String> codeList, List<JSONObject> tableDictList) {
        Map<String, List<DragDictModel>> map = new HashMap<>();
        if (codeList == null) {
            return map;
        }
        for (String code : codeList) {
            map.put(code, getDictItems(code));
        }
        // tableDictList：可后续用 JDBC 查 dictTable/dictText/dictCode；首期留空即可
        return map;
    }

    /** 设计器写操作日志：首期打日志；后续可写入 sys_oper_log。 */
    @Override
    public void addLog(DragLogDTO dto) {
        if (dto != null) {
            log.info("[jimubi] drag log: {}", dto);
        }
    }

    @Override
    public void addLog(String logMsg, int logType, int operateType) {
        log.info("[jimubi] drag logMsg={}, logType={}, operateType={}", logMsg, logType, operateType);
    }
}
