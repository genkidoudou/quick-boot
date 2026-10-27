package io.github.genkidoudou.common.message;

import java.util.List;

/**
 * 一次发送请求。
 * <p>
 * 示例代码，不参与编译。来源不放在这个对象里：业务入口固定记为 API，
 * 管理端控制器调用内部方法时另传 ADMIN，避免业务代码把自己标成手工发送。
 *
 * @param channelIds 通道 UUID。编排层按顺序投递，并先去掉重复 ID
 * @param title      标题，最长 200
 * @param content    正文
 * @param receivers  本次接收人。null 或空表示未传，站内信改用通道默认接收人
 * @param bizType    追溯类型，如 flow、job。不参与去重
 * @param bizId      追溯单号。不参与去重，重复调用会再发一遍
 * @param bizUrl     站内信点击地址。机器人消息不会自动拼接这个地址
 */
public record MessageSendCommand(
        List<String> channelIds,
        String title,
        String content,
        Receivers receivers,
        String bizType,
        String bizId,
        String bizUrl) {

    /**
     * 接口传入的接收人。存的是用户 ID 和部门 ID，不是姓名。
     * <p>
     * 部门在发送时展开为自身及全部下级，只保留状态正常的用户。
     * 这个对象非空时，本次选中的每一条站内信都使用这同一批人。
     */
    public record Receivers(List<Long> userIds, List<Long> deptIds) {

        /**
         * 未传和空列表对站内信是两种输入里的同一种结果：都去走通道默认接收人。
         * 因此空列表在进入编排前应规范成 null。
         */
        public boolean empty() {
            boolean noUser = userIds == null || userIds.isEmpty();
            boolean noDept = deptIds == null || deptIds.isEmpty();
            return noUser && noDept;
        }
    }
}
