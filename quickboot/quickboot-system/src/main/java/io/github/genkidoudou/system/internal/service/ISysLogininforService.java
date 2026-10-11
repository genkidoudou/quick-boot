package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.vo.SysLogininforVo;

/**
 * 登录日志服务。仅查询，不提供新增、修改、删除。
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
}
