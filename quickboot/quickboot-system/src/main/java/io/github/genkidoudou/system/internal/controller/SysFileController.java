package io.github.genkidoudou.system.internal.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.file.FileAccessService;
import io.github.genkidoudou.common.idempotency.Idempotent;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.system.internal.service.ISysFileClassifyService;
import io.github.genkidoudou.system.internal.service.ISysFileService;
import io.github.genkidoudou.system.internal.vo.SysFileClassifyVo;
import io.github.genkidoudou.system.internal.vo.SysFileVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("sys/file")
public class SysFileController {

    private static final String VIEW_MARKER = "/sys/file/view/";

    private final ISysFileService sysFileService;
    private final ISysFileClassifyService sysFileClassifyService;
    private final FileAccessService fileAccessService;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @SaCheckPermission("system:file:list")
    @PostMapping("page")
    public R<PageInfo<SysFileVo>> page(@RequestBody PageRequest<SysFileVo> pageRequest) {
        return R.ok(sysFileService.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @SaCheckPermission("system:file:query")
    @GetMapping("{id}")
    public R<SysFileVo> get(@PathVariable Long id) {
        return R.ok(sysFileService.getVoById(id));
    }

    /**
     * 列出可上传的启用分类。
     *
     * @return 启用分类
     */
    @SaCheckPermission("system:file:add")
    @GetMapping("upload-classifies")
    public R<List<SysFileClassifyVo>> uploadClassifies() {
        return R.ok(sysFileClassifyService.listEnabled());
    }

    /**
     * 按分类上传并登记文件。
     *
     * @param classify 分类编码
     * @param file     文件
     * @return 文件记录
     */
    @SaCheckPermission("system:file:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("upload/{classify}")
    public R<SysFileVo> upload(@PathVariable String classify, @RequestParam("file") MultipartFile file) {
        return R.ok(sysFileService.upload(file, classify));
    }

    /**
     * 按相对路径预览文件。地址可直接在浏览器打开，不校验登录。
     *
     * @param request 当前请求，用于截取 view 之后的相对路径
     * @return 文件流
     */
    @GetMapping("view/**")
    public ResponseEntity<Resource> view(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int idx = uri.indexOf(VIEW_MARKER);
        if (idx < 0) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "文件路径不能为空");
        }
        String relativePath = uri.substring(idx + VIEW_MARKER.length());
        if (!StringUtils.hasText(relativePath)) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "文件路径不能为空");
        }
        fileAccessService.assertPreviewAllowed(relativePath);
        FileAccessService.PreviewPayload payload = fileAccessService.openForPreview(relativePath);
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.hasText(payload.contentType())) {
            headers.setContentType(MediaType.parseMediaType(payload.contentType()));
        }
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(payload.fileName(), StandardCharsets.UTF_8)
                .build());
        return ResponseEntity.ok().headers(headers).body(payload.resource());
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:file:add")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) SysFileVo body) {
        sysFileService.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @SaCheckPermission("system:file:edit")
    @Idempotent(ttlSeconds = 10, key = "#userId")
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) SysFileVo body) {
        sysFileService.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @SaCheckPermission("system:file:remove")
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        sysFileService.removeByIds(ids);
        return R.ok();
    }
}
