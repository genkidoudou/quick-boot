package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysFileClassify;
import io.github.genkidoudou.system.internal.mapper.SysFileClassifyMapper;
import io.github.genkidoudou.system.internal.service.ISysFileClassifyService;
import io.github.genkidoudou.system.internal.service.ISysFileService;
import io.github.genkidoudou.system.internal.vo.SysFileClassifyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件分类服务实现。
 */
@RequiredArgsConstructor
@Service
public class SysFileClassifyServiceImpl extends BaseVoServiceImpl<SysFileClassifyMapper, SysFileClassify, SysFileClassifyVo>
        implements ISysFileClassifyService {


    private final ISysFileService sysFileService;

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysFileClassify> query, SysFileClassifyVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getClassifyCode()), SysFileClassify::getClassifyCode, param.getClassifyCode())
                .like(StrUtil.isNotBlank(param.getClassifyName()), SysFileClassify::getClassifyName, param.getClassifyName())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysFileClassify::getStatus, param.getStatus())
                .eq(StrUtil.isNotBlank(param.getAnonymous()), SysFileClassify::getAnonymous, param.getAnonymous());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysFileClassifyVo getVoById(Long id) {
        SysFileClassifyVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "文件分类不存在");
        }
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
    public SysFileClassify saveVo(SysFileClassifyVo vo) {
        // code 去重
        if (this.baseMapper.selectCount(new LambdaQueryWrapper<SysFileClassify>()
                .eq(SysFileClassify::getClassifyCode, vo.getClassifyCode())) > 0) {
            throw new WarningException(10012, vo.getClassifyCode());
        }
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
    public boolean updateVoById(SysFileClassifyVo vo) {
        if (this.baseMapper.selectCount(new LambdaQueryWrapper<SysFileClassify>()
                .eq(SysFileClassify::getClassifyCode, vo.getClassifyCode())
                .ne(SysFileClassify::getId, vo.getId())) > 0) {
            throw new WarningException(10012, vo.getClassifyCode());
        }
        return super.updateVoById(vo);
    }

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    @Override
    public void removeByIds(List<Long> ids) {
        // 检查该分类下是否有文件,有文件不允许删除

        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        List<SysFileClassify> sysFileClassifies = this.baseMapper.selectByIds(ids);
        for (SysFileClassify sysFileClassify : sysFileClassifies) {

            Long count = sysFileService.count(sysFileClassify.getClassifyCode());
            if (count > 0) {
                throw new WarningException(10013, sysFileClassify.getClassifyName());
            }
        }
        deleteByIds(ids);
    }

    /**
     * 列出启用中的上传分类。
     *
     * @return 状态正常的分类
     */
    @Override
    public List<SysFileClassifyVo> listEnabled() {
        List<SysFileClassify> rows = this.baseMapper.selectList(new LambdaQueryWrapper<SysFileClassify>()
                .eq(SysFileClassify::getStatus, "0")
                .orderByAsc(SysFileClassify::getClassifyCode));
        return rows.stream().map(row -> {
            SysFileClassifyVo vo = new SysFileClassifyVo();
            vo.setId(row.getId());
            vo.setClassifyCode(row.getClassifyCode());
            vo.setClassifyName(row.getClassifyName());
            vo.setAllowExt(row.getAllowExt());
            vo.setMaxSizeBytes(row.getMaxSizeBytes());
            vo.setMaxCount(row.getMaxCount());
            vo.setStatus(row.getStatus());
            vo.setAnonymous(row.getAnonymous());
            return vo;
        }).toList();
    }
}
