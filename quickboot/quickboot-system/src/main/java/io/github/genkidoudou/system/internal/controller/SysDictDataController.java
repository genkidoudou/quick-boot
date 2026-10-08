package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysDictDataService;
import io.github.genkidoudou.system.internal.vo.SysDictDataVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典数据管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/dict/data")
public class SysDictDataController {

    private final ISysDictDataService sysDictDataService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:dictData:list")
    @PostMapping("page")
    public R<PageInfo<SysDictDataVo>> page(@RequestBody PageRequest<SysDictDataVo> pageRequest) {
        return R.ok(sysDictDataService.pageVo(pageRequest));
    }

    /**
     * 按字典类型查询启用项（供前端 useDict 使用；登录即可，不挂资源权限字）。
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表
     */
    @GetMapping("type/{dictType}")
    public R<List<SysDictDataVo>> listByType(@PathVariable String dictType) {
        return R.ok(sysDictDataService.listByDictType(dictType));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:dictData:query")
    @GetMapping("{id}")
    public R<SysDictDataVo> get(@PathVariable Long id) {
        return R.ok(sysDictDataService.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:dictData:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysDictDataVo body) {
        sysDictDataService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:dictData:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysDictDataVo body) {
        sysDictDataService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:dictData:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysDictDataService.removeByIds(ids);
        return R.ok();
    }


}
