CREATE TABLE `tmp_report_data_income` (
  `biz_income` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `bx_jj_yongjin` decimal(10,2) DEFAULT NULL,
  `bx_zx_money` decimal(10,2) DEFAULT NULL,
  `chengbao_gz_money` decimal(10,2) DEFAULT NULL,
  `bx_gg_moeny` decimal(10,2) DEFAULT NULL,
  `tb_zx_money` decimal(10,2) DEFAULT NULL,
  `neikong_zx_money` decimal(10,2) DEFAULT NULL,
  `total` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
