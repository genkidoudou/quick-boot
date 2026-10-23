package io.github.genkidoudou.job.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.job.internal.entity.SysJob;
import io.github.genkidoudou.job.internal.enums.JobTriggerType;
import io.github.genkidoudou.job.internal.mapper.SysJobMapper;
import io.github.genkidoudou.job.internal.quartz.ITask;
import io.github.genkidoudou.job.internal.quartz.JobExecutor;
import io.github.genkidoudou.job.internal.quartz.JobSchedulerEngine;
import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.JobTaskBeanVo;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定时任务服务实现。
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysJobServiceImpl extends BaseVoServiceImpl<SysJobMapper, SysJob, SysJobVo>
        implements ISysJobService {

    private static final Set<String> JOB_TYPES = Set.of("BEAN", "SHELL", "PYTHON");
    private static final Set<String> PYTHON_BINS = Set.of("python2", "python3");

    private final JobSchedulerEngine jobSchedulerEngine;
    private final JobExecutor jobExecutor;
    private final ApplicationContext applicationContext;

    @Override
    public void applyQuery(LambdaQueryWrapper<SysJob> query, SysJobVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getJobName()), SysJob::getJobName, param.getJobName())
                .eq(StrUtil.isNotBlank(param.getJobType()), SysJob::getJobType, param.getJobType())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysJob::getStatus, param.getStatus());
    }

    @Override
    public SysJobVo getVoById(Long id) {
        SysJobVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysJob saveVo(SysJobVo vo) {
        normalize(vo);
        SysJob sysJob = super.saveVo(vo);
        vo.setId(sysJob.getId());
        jobSchedulerEngine.addJob(vo);
        return sysJob;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysJobVo vo) {
        normalize(vo);
        boolean ok = super.updateVoById(vo);
        if (ok) {
            jobSchedulerEngine.updateJob(getVoById(vo.getId()));
        }
        return ok;
    }

    @Override
    public void removeByIds(List<Long> ids) {
        for (Long id : ids) {
            jobSchedulerEngine.deleteJob(getVoById(id));
        }
        deleteByIds(ids);
    }

    /**
     * 只改状态并同步调度：updateJob 会按启停决定是否注册，不再额外 pause/resume。
     *
     * @param jobId  任务 id
     * @param status 目标状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long jobId, String status) {
        SysJob patch = new SysJob();
        patch.setId(jobId);
        patch.setStatus(status);
        this.updateById(patch);
        jobSchedulerEngine.updateJob(getVoById(jobId));
    }

    /**
     * 立即执行走执行器，不依赖 Quartz 是否已挂载该 Job。
     *
     * @param jobId 任务 id
     */
    @Override
    public void run(Long jobId) {
        getVoById(jobId);
        jobExecutor.execute(jobId, JobTriggerType.MANUAL.getValue());
    }

    @Override
    public List<SysJobVo> listEnabled() {
        return listVo(new LambdaQueryWrapper<SysJob>()
                .eq(SysJob::getStatus, CommonEnums.STATUS_ENABLE.getValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JobTaskBeanVo> listTaskBeans() {
        Map<String, ITask> beans = applicationContext.getBeansOfType(ITask.class);
        List<JobTaskBeanVo> list = new ArrayList<>(beans.size());
        for (Map.Entry<String, ITask> entry : beans.entrySet()) {
            Class<?> targetClass = AopUtils.getTargetClass(entry.getValue());
            list.add(new JobTaskBeanVo(entry.getKey(), targetClass.getName()));
        }
        list.sort(Comparator.comparing(JobTaskBeanVo::getBeanName));
        return list;
    }

    /**
     * 按任务类型收口字段。
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
