package io.github.genkidoudou.msg.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.msg.internal.entity.MsgInbox;
import io.github.genkidoudou.msg.internal.vo.MsgInboxVo;

import java.util.List;

/**
 * 站内信收件箱服务。
 */
public interface IMsgInboxService {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<MsgInboxVo> pageVo(PageRequest<MsgInboxVo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    MsgInboxVo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    MsgInbox saveVo(MsgInboxVo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById(MsgInboxVo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);

    /**
     * 批量插入
     *
     * @param msgInboxes 站内信
     * @return
     * @since 2026/11/2
     */
    void saveBatch(List<MsgInbox> msgInboxes);

}
