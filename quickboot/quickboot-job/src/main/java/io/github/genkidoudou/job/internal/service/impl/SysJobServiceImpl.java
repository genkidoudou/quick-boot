package io.github.genkidoudou.job.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.job.internal.entity.SysJob;
import io.github.genkidoudou.job.internal.mapper.SysJobMapper;
import io.github.genkidoudou.job.internal.quartz.ITask;
import io.github.genkidoudou.job.internal.quartz.JobSchedulerEngine;
import io.github.genkidoudou.job.internal.service.ISysJobService;
import io.github.genkidoudou.job.internal.vo.JobTaskBeanVo;
import io.github.genkidoudou.job.internal.vo.SysJobVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

    /** 详情页展示「接下来几次」预计执行时间的条数。 */
    private static final int NEXT_FIRE_COUNT = 5;

    private final JobSchedulerEngine jobSchedulerEngine;
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

    /**
     * 详情：基础字段 + 根据 Cron 推算接下来若干次执行时间。
     */
    @Override
    public SysJobVo getVoById(Long id) {
        SysJobVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务不存在");
        }
        vo.setNextFireTimes(computeNextFireTimes(vo.getCronExpression(), NEXT_FIRE_COUNT));
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

    /**
     * 仅停用状态允许修改配置；正常（启用）中请先停用。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysJobVo vo) {
        assertDisabled(vo.getId());
        normalize(vo);
        boolean ok = super.updateVoById(vo);
        if (ok) {
            jobSchedulerEngine.updateJob(getVoById(vo.getId()));
        }
        return ok;
    }

    /**
     * 仅停用状态允许删除；正常（启用）中请先停用。
     */
    @Override
    public void removeByIds(List<Long> ids) {
        for (Long id : ids) {
            assertDisabled(id);
            jobSchedulerEngine.deleteJob(getVoById(id));
        }
        deleteByIds(ids);
    }

    /**
     * 只改状态并同步调度：updateJob 会按启停决定是否注册，不再额外 pause/resume。
     * 启停本身不受「须先停用」限制（否则无法从正常切到停用）。
     * <p>
     * 必须用 {@link LambdaUpdateWrapper} 只更新 status：
     * {@code invokeTarget}/{@code scriptContent}/{@code pythonBin} 配置了
     * {@code FieldStrategy.ALWAYS}，若用 {@code updateById(仅含 id+status 的实体)}
     * 会把这些字段写成 null，表现为「停用后 Bean 名变空」。
     *
     * @param jobId  任务 id
     * @param status 目标状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long jobId, String status) {
        boolean ok = this.update(new LambdaUpdateWrapper<SysJob>()
                .eq(SysJob::getId, jobId)
                .set(SysJob::getStatus, status));
        if (!ok) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务不存在");
        }
        jobSchedulerEngine.updateJob(getVoById(jobId));
    }

    /**
     * 执行一次：正常 / 停用均可。
     * 走 Quartz 同一 JobKey（停用时由引擎挂 durable JobDetail，不挂 Cron）。
     *
     * @param jobId 任务 id
     */
    @Override
    public void run(Long jobId) {
        jobSchedulerEngine.triggerOnce(getVoById(jobId));
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
     * 正常（启用）状态下禁止修改配置与删除，须先停用。
     *
     * @param jobId 任务 id
     */
    private void assertDisabled(Long jobId) {
        // 直接读库状态，避免 getVoById 附带的 nextFireTimes 计算开销；也不依赖入参里的 status
        SysJob row = this.getById(jobId);
        if (row == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "定时任务不存在");
        }
        if (CommonEnums.STATUS_ENABLE.getValue().equals(row.getStatus())) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM,
                    "任务处于正常状态，请先停用后再修改或删除");
        }
    }

    /**
     * 按 Cron 从当前时刻起向后推算接下来若干次触发时间。
     * Cron 非法时返回空列表（不阻断详情查看）。
     *
     * @param cronExpression Quartz Cron
     * @param count          条数
     * @return 预计执行时间列表
     */
    private List<LocalDateTime> computeNextFireTimes(String cronExpression, int count) {
        List<LocalDateTime> times = new ArrayList<>();
        if (StrUtil.isBlank(cronExpression) || count <= 0) {
            return times;
        }
        try {
            CronExpression cron = new CronExpression(cronExpression.trim());
            Date cursor = new Date();
            ZoneId zone = ZoneId.systemDefault();
            for (int i = 0; i < count; i++) {
                Date next = cron.getNextValidTimeAfter(cursor);
                if (next == null) {
                    break;
                }
                times.add(LocalDateTime.ofInstant(next.toInstant(), zone));
                cursor = next;
            }
        } catch (ParseException ex) {
            log.warn("Cron 无法解析，跳过下次执行时间推算: {}", cronExpression, ex);
        }
        return times;
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
