package io.github.genkidoudou.job.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.job.internal.vo.SysJobLogVo;

import java.util.List;

/**
 * 定时任务日志服务。不提供新增和修改。
 */
public interface ISysJobLogService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysJobLogVo> pageVo(PageRequest<SysJobLogVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysJobLogVo getVoById(Long id);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);
}
