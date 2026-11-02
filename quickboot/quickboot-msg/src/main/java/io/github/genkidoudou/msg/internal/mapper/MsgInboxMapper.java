package io.github.genkidoudou.msg.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.msg.internal.entity.MsgInbox;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内信收件箱 Mapper。
 */
@Mapper
public interface MsgInboxMapper extends BaseBaseMapper<MsgInbox> {
}
