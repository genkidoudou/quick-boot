package io.github.genkidoudou.msg.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecordItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送明细 Mapper。
 */
@Mapper
public interface MsgSendRecordItemMapper extends BaseBaseMapper<MsgSendRecordItem> {
}
