package io.github.genkidoudou.system.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.system.internal.entity.SysFileClassify;
import io.github.genkidoudou.system.internal.vo.SysFileClassifyVo;

import java.util.List;

/**
 * 文件分类服务。
 */
public interface ISysFileClassifyService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<SysFileClassifyVo> pageVo(PageRequest<SysFileClassifyVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    SysFileClassifyVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    SysFileClassify saveVo(SysFileClassifyVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(SysFileClassifyVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 列出启用中的上传分类，供文件上传选择。
     *
     * @return 状态正常的分类
     */
    List<SysFileClassifyVo> listEnabled();
}
