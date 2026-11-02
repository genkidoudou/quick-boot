package io.github.genkidoudou.msg.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.msg.internal.entity.MsgChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息渠道 Mapper。
 */
@Mapper
public interface MsgChannelMapper extends BaseBaseMapper<MsgChannel> {
}
