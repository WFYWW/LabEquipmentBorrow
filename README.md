# 校园实验设备借用管理系统（LabEquipmentBorrow）

Java EE 课程设计项目：Struts 2 + MySQL 的实验设备借用管理 WAR 工程。

## v1.1 新增功能（2026-11）

1. **管理员 / 学员双角色**
   - 内置管理员账号 `admin / 123456`（`user.role = 'admin'`）；
   - 只有管理员可以新增 / 编辑 / 删除设备（含图片设置），普通学员操作会跳提示页；
   - 顶部导航栏显示当前用户身份徽章。
2. **设备图片**
   - `equipment.image` 字段存 webapp 下相对路径，内置 7 张演示设备实拍图（`webapp/images/equip/`）；
   - 设备列表缩略图、借用页大图、编辑页预览；
   - 管理员表单支持**上传图片**（jpg/jpeg/png/gif/webp，≤5MB，存到 `images/equip/`）或选择预设图；
   - 未设置图片的设备统一显示默认占位图 `images/equip/default.jpg`。
3. **借用记录权限**
   - 学员只能看到**自己的**借用记录（所有筛选页签均只作用于本人数据）；
   - 管理员可查看全部记录，并可通过"我的借用"页签只看自己；
   - 学员只能归还自己借的设备，管理员可代还任意记录；
   - 首页"逾期提醒 / 3 天内到期"与导航栏逾期角标同样按角色过滤（管理员=全站，学员=本人）。

## 演示账号

| 账号 | 密码 | 角色 | 用途 |
| --- | --- | --- | --- |
| admin | 123456 | 管理员 | 管理设备信息（增删改 + 图片）、查看全部借用记录 |
| zhangsan | 123456 | 学员 | 有逾期未还 → 演示"禁止再借"；只能看自己的借用记录 |
| lisi | 123456 | 学员 | 正常用户 |

## 数据库

- 库名 `lab_equip`，脚本在 `db/`：
  - `init.sql`：全量初始化（**会重建整个数据库**，含 v1.1 的 role/image 字段、admin 账号、演示数据及图片路径）；
  - `upgrade-v1.1.sql`：旧库增量升级（不清数据：加 role/image 字段、插 admin、给演示设备补图片路径）。

```bash
mysql -u root -p --default-character-set=utf8mb4 < db/init.sql
# 或旧库升级：
mysql -u root -p --default-character-set=utf8mb4 < db/upgrade-v1.1.sql
```

## 构建与运行

```bash
# 本机 Maven 3.9.9 在 D:\maven\apache-maven-3.9.9\bin\mvn.cmd
mvn -DskipTests package        # 产物 target/labequip.war，访问路径 /labequip/
```

- 本机调试：IDEA 配置 Tomcat 9（部署 context `/labequip`）；
- 服务器部署：把 `labequip.war` 放入 Tomcat `webapps/`（Ubuntu 虚拟机为 `/opt/tomcat/webapps/`）。

## 目录速览

```
src/main/java/com/labequip/
  action/    BaseAction(isAdmin) / Login / Dashboard / Equipment(含图片上传) / Borrow
  dao/       UserDao / EquipmentDao / BorrowRecordDao
  entity/    User(role) / Equipment(image) / BorrowRecord
  interceptor/LoginInterceptor（登录校验）
  util/      DBUtil / DateUtil / EncodingFilter
src/main/resources/struts.xml      动作映射、multipart 上限 5MB
src/main/webapp/pages/             dashboard / equipment-* / borrow-* / message
src/main/webapp/images/equip/      内置设备图片与默认占位图
db/                                init.sql / upgrade-v1.1.sql
```

> 课程演示项目：密码明文存储、图片上传保留旧文件不删除，仅用于教学演示，勿用于生产。
