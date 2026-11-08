package io.github.genkidoudou.msg.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息渠道视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsgChannelVo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @NotNull(message = "消息渠道ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 渠道名称。
     */
    @NotBlank(message = "渠道名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 255, message = "渠道名称不能超过255个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 渠道类型（MSG_CHANNEL_TYPE）。
     */
    @NotBlank(message = "渠道类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Size(max = 3, message = "渠道类型不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String channelType;

    /**
     * Webhook URL，仅 Webhook 渠道使用。
     */
    @Size(max = 512, message = "Webhook URL不能超过512个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String webhookUrl;

    /**
     * Spring Bean 名称，仅 Bean 渠道使用。
     */
    @Size(max = 255, message = "Bean 名称不能超过255个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String beanName;

    /**
     * 模板内容。空或空白则发送时直接用正文。
     */
    private String templateContent;

    /**
     * 状态（COMMON_STATUS）。
     */
    @Size(max = 3, message = "状态不能超过3个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String status;

    /**
     * Webhook 请求方式：GET-FORM、POST-FORM、POST-JSON。
     */
    @Size(max = 32, message = "Webhook请求方式不能超过32个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String webhookMethod;

    /**
     * Webhook 成功规则。空则 HTTP 2xx 即成功。
     * 否则为 JSON 字段=期望值，例如 errcode=0、data.success=true。
     */
    @Size(max = 255, message = "成功规则不能超过255个字符", groups = {AddGroup.class, UpdateGroup.class})
    private String webhookSuccess;
}
