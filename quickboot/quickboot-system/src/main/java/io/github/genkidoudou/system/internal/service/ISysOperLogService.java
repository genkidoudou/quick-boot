package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.vo.SysOperLogVo;

/**
 * 操作日志服务。查询 + 切面写入；不提供修改、删除。
 */
public interface ISysOperLogService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysOperLogVo> pageVo(PageRequest<SysOperLogVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysOperLogVo getVoById(Long id);

    /**
     * 写入一条操作日志。
     *
     * @param vo 操作访问信息
     */
    void record(SysOperLogVo vo);
}
