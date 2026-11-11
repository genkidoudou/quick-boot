CREATE TABLE `tmp_report_data_1` (
  `monty` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '月份',
  `main_income` decimal(10,2) DEFAULT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  `his_lowest` decimal(10,2) DEFAULT NULL,
  `his_average` decimal(10,2) DEFAULT NULL,
  `his_highest` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
