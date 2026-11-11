package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.entity.SysOauthClient;
import io.github.genkidoudou.system.internal.vo.SysOauthClientVo;

import java.util.List;

/**
 * OAuth 客户端服务。
 */
public interface ISysOauthClientService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysOauthClientVo> pageVo(PageRequest<SysOauthClientVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysOauthClientVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysOauthClient saveVo(SysOauthClientVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysOauthClientVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);
}
