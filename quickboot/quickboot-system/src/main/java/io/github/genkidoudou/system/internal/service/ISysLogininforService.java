package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.vo.SysLogininforVo;

/**
 * 登录日志服务。查询 + 登录时写入；不提供修改、删除。
 */
public interface ISysLogininforService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysLogininforVo> pageVo(PageRequest<SysLogininforVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysLogininforVo getVoById(Long id);

    /**
     * 写入一条登录日志。
     *
     * @param vo 登录访问信息
     */
    void record(SysLogininforVo vo);
}
