package io.github.genkidoudou.msg.internal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息群组实体，与表 {@code msg_group} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("msg_group")
public class MsgGroup extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 群组名称。
     */
    private String name;


    /**
     * 成员类型(MSG_GROUP_MEMBER_TYPE)
     *
     * @since 2026/10/31
     */

    private String memberType;
}
