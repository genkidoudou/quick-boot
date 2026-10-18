package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.file.FileAccessService;
import io.github.genkidoudou.common.file.FilePathSupport;
import io.github.genkidoudou.common.file.FileUploadResult;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysFile;
import io.github.genkidoudou.system.internal.entity.SysUser;
import io.github.genkidoudou.system.internal.mapper.SysFileMapper;
import io.github.genkidoudou.system.internal.mapper.SysUserMapper;
import io.github.genkidoudou.system.internal.service.ISysFileService;
import io.github.genkidoudou.system.internal.vo.SysFileVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文件服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends BaseVoServiceImpl<SysFileMapper, SysFile, SysFileVo>
        implements ISysFileService {

    private final FileAccessService fileAccessService;
    private final SysUserMapper sysUserMapper;

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysFile> query, SysFileVo param) {
        if (param != null) {
            query.like(StrUtil.isNotBlank(param.getClassifyCode()), SysFile::getClassifyCode, param.getClassifyCode())
                    .like(StrUtil.isNotBlank(param.getOriginalName()), SysFile::getOriginalName, param.getOriginalName())
                    .eq(StrUtil.isNotBlank(param.getExt()), SysFile::getExt, param.getExt());
        }
        query.orderByDesc(SysFile::getCreateTime);
    }

    /**
     * 分页查询，并补上传人显示名。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @Override
    public PageInfo<SysFileVo> pageVo(PageRequest<SysFileVo> pageRequest) {
        PageInfo<SysFileVo> page = super.pageVo(pageRequest);
        fillCreateName(page.getRecords());
        return page;
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysFileVo getVoById(Long id) {
        SysFileVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "文件不存在");
        }
        fillCreateName(List.of(vo));
        return vo;
    }

    /**
     * 新增；带事务。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile saveVo(SysFileVo vo) {
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysFileVo vo) {
        return super.updateVoById(vo);
    }

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    @Override
    public void removeByIds(List<Long> ids) {
        deleteByIds(ids);
    }

    /**
     * 按分类上传并登记。存储相对路径入库，返回视图时由 {@code @FileUrl} 拼访问地址。
     *
     * @param file         上传文件
     * @param classifyCode 分类编码
     * @return 登记后的文件视图
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVo upload(MultipartFile file, String classifyCode) {
        FileUploadResult result = fileAccessService.upload(file, classifyCode);
        SysFile row = new SysFile();
        row.setClassifyCode(result.getClassify());
        row.setOriginalName(result.getFileName());
        row.setPath(result.getRelativePath());
        row.setExt(FilePathSupport.normalizeExtension(result.getFileName()));
        row.setSizeBytes(file.getSize());
        this.save(row);
        return getVoById(row.getId());
    }

    /**
     * 按创建人用户 ID 填上传人显示名：优先昵称，否则用户名。
     *
     * @param records 文件视图
     */
    private void fillCreateName(List<SysFileVo> records) {
        if (CollectionUtil.isEmpty(records)) {
            return;
        }
        Set<Long> userIds = new LinkedHashSet<>();
        for (SysFileVo row : records) {
            Long userId = parseUserId(row.getCreateBy());
            if (userId != null) {
                userIds.add(userId);
            }
        }
        Map<Long, String> names = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectByIds(userIds);
            if (users != null) {
                for (SysUser user : users) {
                    names.put(user.getId(), StrUtil.blankToDefault(user.getNickName(), user.getUserName()));
                }
            }
        }
        for (SysFileVo row : records) {
            Long userId = parseUserId(row.getCreateBy());
            if (userId == null) {
                row.setCreateName(row.getCreateBy());
                continue;
            }
            row.setCreateName(names.getOrDefault(userId, row.getCreateBy()));
        }
    }

    /**
     * @param createBy 创建人，存的是用户 ID 字符串
     * @return 用户 ID；无法解析时返回 null
     */
    private Long parseUserId(String createBy) {
        if (StrUtil.isBlank(createBy)) {
            return null;
        }
        try {
            return Long.valueOf(createBy.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public Long count(String classifyCode) {
        return this.baseMapper.selectCount(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getClassifyCode, classifyCode));
    }
}
