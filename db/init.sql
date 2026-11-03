-- ============================================================
-- 校园实验设备借用管理系统 数据库初始化脚本
-- 数据库: lab_equip    字符集: utf8mb4
-- 用法: mysql -u root -p --default-character-set=utf8mb4 < init.sql
-- 说明: 示例数据使用 CURDATE() 相对日期，任何时候执行都有效：
--       保证永远存在 1 条"逾期未还"记录（用于逾期提醒/禁止再借演示）
-- ============================================================

DROP DATABASE IF EXISTS lab_equip;
CREATE DATABASE lab_equip DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE lab_equip;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
CREATE TABLE `user` (
  `id`          INT PRIMARY KEY AUTO_INCREMENT,
  `username`    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录名',
  `password`    VARCHAR(100) NOT NULL             COMMENT '密码（明文，课程演示用）',
  `real_name`   VARCHAR(50)  NOT NULL             COMMENT '真实姓名',
  `student_no`  VARCHAR(20)  DEFAULT ''           COMMENT '学号',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) COMMENT='用户表';

-- ------------------------------------------------------------
-- 2. 设备表
--    status: 在库 / 借出 / 维修 / 报废
-- ------------------------------------------------------------
CREATE TABLE `equipment` (
  `id`          INT PRIMARY KEY AUTO_INCREMENT,
  `name`        VARCHAR(100) NOT NULL             COMMENT '设备名称',
  `model`       VARCHAR(100) DEFAULT ''           COMMENT '规格型号',
  `location`    VARCHAR(100) DEFAULT ''           COMMENT '存放位置',
  `status`      VARCHAR(20)  NOT NULL DEFAULT '在库' COMMENT '设备状态',
  `description` VARCHAR(500) DEFAULT ''           COMMENT '备注说明',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT='实验设备表';

-- ------------------------------------------------------------
-- 3. 借用记录表
--    status: 借出中 / 已归还
--    逾期判断(实时计算，不落库): return_date IS NULL AND due_date < CURDATE()
-- ------------------------------------------------------------
CREATE TABLE `borrow_record` (
  `id`          INT PRIMARY KEY AUTO_INCREMENT,
  `equipment_id` INT NOT NULL COMMENT '设备ID',
  `user_id`      INT NOT NULL COMMENT '借用人ID',
  `borrow_date`  DATE NOT NULL COMMENT '借出日期',
  `due_date`     DATE NOT NULL COMMENT '应还日期',
  `return_date`  DATE NULL     COMMENT '实际归还日期(NULL=未还)',
  `status`       VARCHAR(20) NOT NULL DEFAULT '借出中' COMMENT '记录状态',
  CONSTRAINT fk_record_equipment FOREIGN KEY (equipment_id) REFERENCES `equipment`(id),
  CONSTRAINT fk_record_user      FOREIGN KEY (user_id)      REFERENCES `user`(id)
) COMMENT='设备借用记录表';

-- ============================================================
-- 示例数据
-- ============================================================

-- 用户（密码均为明文演示）：zhangsan 有逾期记录 → 演示"禁止再借"；lisi 正常
INSERT INTO `user` (username, password, real_name, student_no) VALUES
('zhangsan', '123456', '张三', '2024001101'),
('lisi',     '123456', '李四', '2024001102');

-- 设备：1/2 号处于"借出"（与下方借用记录对应）
INSERT INTO `equipment` (name, model, location, status, description) VALUES
('数字示波器',     'RIGOL DS1054Z',        '实验楼A-301', '借出', '四通道，100MHz带宽，随附探头4根'),
('台式万用表',     'Fluke 15B+',           '实验楼A-301', '借出', '真有效值测量，需归还时附表笔'),
('函数信号发生器', 'SDG1032X',             '实验楼A-305', '在库', '双通道，最高60MHz'),
('直流稳压电源',   'DP832A',               '实验楼A-305', '在库', '三路输出，最高32V/3A'),
('光学显微镜',     'Nikon E100',           '实验楼B-201', '在库', '生物显微镜，40-1000倍'),
('3D打印机',       'Prusa i3 MK4',         '实训中心-103', '维修', '喷头堵塞维修中，暂停外借'),
('频谱分析仪',     'Siglent SSA1015X',     '实验楼A-307', '报废', '显示屏损坏，已申请报废');

-- 借用记录（使用相对日期，保证演示效果恒定）
-- 张三：逾期未还（示波器，应还日=2天前）→ 触发逾期提醒 + 禁止再借
INSERT INTO `borrow_record` (equipment_id, user_id, borrow_date, due_date, return_date, status)
VALUES (1, 1, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, '借出中');

-- 李四：借出中、未到期（万用表，应还日=7天后）→ 正常
INSERT INTO `borrow_record` (equipment_id, user_id, borrow_date, due_date, return_date, status)
VALUES (2, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), NULL, '借出中');

-- 李四：3天内即将到期（信号发生器，应还日=2天后）→ 演示"即将到期"黄色提醒
INSERT INTO `borrow_record` (equipment_id, user_id, borrow_date, due_date, return_date, status)
VALUES (3, 2, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), NULL, '借出中');
UPDATE `equipment` SET status='借出' WHERE id=3;

-- 历史记录：已按期归还
INSERT INTO `borrow_record` (equipment_id, user_id, borrow_date, due_date, return_date, status)
VALUES (4, 1, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(CURDATE(), INTERVAL 7 DAY), '已归还');

-- 验证
SELECT '用户' AS t, COUNT(*) AS n FROM `user`
UNION ALL SELECT '设备', COUNT(*) FROM `equipment`
UNION ALL SELECT '借用记录', COUNT(*) FROM `borrow_record`;
