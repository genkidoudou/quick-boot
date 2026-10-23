package io.github.genkidoudou.job.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.job.internal.entity.SysJob;
import io.github.genkidoudou.job.internal.vo.JobTaskBeanVo;
import io.github.genkidoudou.job.internal.vo.SysJobVo;

import java.util.List;

/**
 * 定时任务服务。
 */
public interface ISysJobService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysJobVo> pageVo(PageRequest<SysJobVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysJobVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysJob saveVo(SysJobVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysJobVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);


    /**
     * 修改状态
     *
     * @param jobId  定时任务
     * @param status 状态
     * @return
     * @since 2026/10/22
     */
    void changeStatus(Long jobId, String status);

    /**
     * 立即执行一次（不依赖 Quartz 是否已注册该 Job）。
     *
     * @param jobId 任务 id
     */
    void run(Long jobId);

    /**
     * 列出启用中的任务，供启动时装载调度。
     *
     * @return 启用任务列表
     */
    List<SysJobVo> listEnabled();

    /**
     * 列出容器中所有实现 {@code ITask} 的 Bean，供管理端选择 invokeTarget。
     *
     * @return Bean 名称与类名列表
     */
    List<JobTaskBeanVo> listTaskBeans();
}
