CREATE TABLE `chat2bi_message` (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `conversation_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属会话id',
  `role` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色 user/ai',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '文本内容（预览/检索用）',
  `msg_json` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '完整消息对象JSON（含dsl/data/sql/chartType/insight等，无损还原）',
  `sort_no` int DEFAULT NULL COMMENT '顺序号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_c2b_msg_conv` (`conversation_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Chat2BI 对话消息';
