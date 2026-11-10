-- ============================================================
-- v1.1 增量升级脚本（不重建数据库、不清空已有数据）
-- 适用：已按旧版 init.sql 建好库（user 无 role 字段、equipment 无 image 字段）
-- 用法: mysql -u root -p --default-character-set=utf8mb4 < upgrade-v1.1.sql
-- 注意：重复执行会报 Duplicate column / Duplicate entry 错误，忽略即可
-- ============================================================
USE lab_equip;

-- 1. 用户表增加角色字段（存量用户默认为学员）
ALTER TABLE `user`
  ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色：admin=管理员 / student=学员';

-- 2. 设备表增加图片字段
ALTER TABLE `equipment`
  ADD COLUMN `image` VARCHAR(200) DEFAULT '' COMMENT '设备图片（webapp 下相对路径，如 images/equip/xxx.jpg）';

-- 3. 内置管理员账号（不存在才插入）
INSERT INTO `user` (username, password, real_name, student_no, role)
SELECT 'admin', '123456', '系统管理员', '', 'admin' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'admin');

-- 4. 给旧版演示设备补图片（文件随 WAR 部署在 webapp/images/equip/ 下）
UPDATE `equipment` SET image = 'images/equip/oscilloscope.jpg'      WHERE name = '数字示波器';
UPDATE `equipment` SET image = 'images/equip/multimeter.jpg'        WHERE name = '台式万用表';
UPDATE `equipment` SET image = 'images/equip/signal-generator.jpg'  WHERE name = '函数信号发生器';
UPDATE `equipment` SET image = 'images/equip/power-supply.jpg'      WHERE name = '直流稳压电源';
UPDATE `equipment` SET image = 'images/equip/microscope.jpg'        WHERE name = '光学显微镜';
UPDATE `equipment` SET image = 'images/equip/3d-printer.jpg'        WHERE name = '3D打印机';
UPDATE `equipment` SET image = 'images/equip/spectrum-analyzer.jpg' WHERE name = '频谱分析仪';

-- 验证
SELECT username, real_name, role FROM `user`;
SELECT id, name, image FROM `equipment`;
