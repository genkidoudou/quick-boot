package io.github.genkidoudou.msg.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.msg.internal.entity.MsgSendRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送记录 Mapper。
 */
@Mapper
public interface MsgSendRecordMapper extends BaseBaseMapper<MsgSendRecord> {
}
