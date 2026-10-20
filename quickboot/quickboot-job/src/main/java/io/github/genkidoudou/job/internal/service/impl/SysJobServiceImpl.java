package io.github.genkidoudou.job.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.job.internal.entity.SysJob;
import io.github.genkidoudou.job.internal.mapper.SysJobMapper;
import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 定时任务服务实现。
 */
@Service
public class SysJobServiceImpl extends BaseVoServiceImpl<SysJobMapper, SysJob, SysJobVo>
        implements ISysJobService {

    private static final Set<String> JOB_TYPES = Set.of("BEAN", "SHELL", "PYTHON");
    private static final Set<String> PYTHON_BINS = Set.of("python2", "python3");

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysJob> query, SysJobVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getJobName()), SysJob::getJobName, param.getJobName())
                .eq(StrUtil.isNotBlank(param.getJobType()), SysJob::getJobType, param.getJobType())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysJob::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysJobVo getVoById(Long id) {
        SysJobVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务不存在");
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
    public SysJob saveVo(SysJobVo vo) {
        normalize(vo);
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
    public boolean updateVoById(SysJobVo vo) {
        normalize(vo);
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
     * 按任务类型收口字段：Bean 只留调用目标，脚本任务只留脚本，Python 再校验解释器。
     *
     * @param vo 视图对象
     */
    private void normalize(SysJobVo vo) {
        if (vo == null || !JOB_TYPES.contains(vo.getJobType())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "任务类型不正确");
        }
        if ("BEAN".equals(vo.getJobType())) {
            if (StrUtil.isBlank(vo.getInvokeTarget())) {
                throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "Bean 名称不能为空");
            }
            vo.setScriptContent(null);
            vo.setPythonBin(null);
            return;
        }
        if (StrUtil.isBlank(vo.getScriptContent())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "任务脚本不能为空");
        }
        vo.setInvokeTarget(null);
        if ("PYTHON".equals(vo.getJobType())) {
            if (!PYTHON_BINS.contains(vo.getPythonBin())) {
                throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "Python 解释器只能是 python2 或 python3");
            }
            return;
        }
        vo.setPythonBin(null);
    }
}
