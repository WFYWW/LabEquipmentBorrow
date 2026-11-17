# LabEquipmentBorrow 部署文档（DEPLOY.md）

校园实验设备借用管理系统 —— 从零开始、不依赖开发 IDE 的完整部署指南。
照着本文档操作，任何没有参与过本项目的同学都应能把系统跑起来。

## 1. 项目简介与技术栈

- **项目**：校园实验设备借用管理系统（设备浏览/借用/归还/逾期提醒，管理员-学员双角色）
- **技术栈基线（课程统一要求）**：
  - JavaEE8（`javax.servlet.*`），**未使用** JakartaEE / `jakarta.*`
  - Apache Tomcat **9.x**（不要用 Tomcat 10+，命名空间不兼容）
  - MySQL 8.x（本机开发亦可在 MySQL 9 上运行）
  - Maven 3.8+（本仓库验证版本 3.9.9），Struts 2.5.33，编译目标 Java 8（JDK 8~21 均可编译运行）
- 产物：`target/labequip.war`，访问上下文路径 `/labequip`

## 2. 环境前提

| 组件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 8 / 11 / 17 / 21 | 编译目标为 1.8，建议与运行一致；本仓库用 JDK 21 验证 |
| Maven | 3.8+ | 用于构建 WAR |
| Tomcat | 9.0.x | 必须 9.x（javax 命名空间） |
| MySQL | 8.x+ | 库名 `lab_equip`，字符集 utf8mb4 |
| Git | 任意 | 拉取代码用 |

## 3. 获取代码与构建

```bash
git clone https://github.com/WFYWW/LabEquipmentBorrow.git
cd LabEquipmentBorrow
mvn -DskipTests package        # Windows: 使用 D:\maven\apache-maven-3.9.9\bin\mvn.cmd
# 构建成功后产物: target/labequip.war
```

## 4. 数据库初始化

二选一：

```bash
# 方式 A（推荐，全新环境）：全量初始化（注意：会重建整个 lab_equip 库）
mysql -u root -p --default-character-set=utf8mb4 < db/init.sql

# 方式 B（已有旧版数据）：增量升级，不清数据（重复执行会报 Duplicate column，忽略即可）
mysql -u root -p --default-character-set=utf8mb4 < db/upgrade-v1.1.sql
```

连接参数（应用读取顺序）：环境变量 → 代码内默认值。

| 环境变量 | 默认值 |
| --- | --- |
| `LABEQUIP_DB_URL` | `jdbc:mysql://localhost:3306/lab_equip?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai` |
| `LABEQUIP_DB_USER` | `root` |
| `LABEQUIP_DB_PASSWORD` | `123456` |

> 生产/演示部署建议通过环境变量注入口令，不要改源码里的默认值。

## 5. 部署启动（Tomcat 9）

```bash
# 1) 把 WAR 放入 Tomcat（Linux 示例）
cp target/labequip.war /opt/tomcat/webapps/

# 2) 启动（Linux，systemd 安装时）
sudo systemctl restart tomcat
# Windows: 双击 %CATALINA_HOME%\bin\startup.bat，关闭用 shutdown.bat

# 3) 确认部署成功：看 logs/catalina.out 或 catalina.yyyy-mm-dd.log 中出现
#    "Deployment of deployment descriptor ... /labequip ... has finished in ... ms"
```

## 6. 部署后验证

| 验证点 | 方式 | 预期 |
| --- | --- | --- |
| 健康检查 | 浏览器访问 `http://<host>:8080/labequip/health` | `{"app":"labequip","status":"ok","db":"UP","userCount":4,...}` |
| 登录页 | 访问 `http://<host>:8080/labequip/` | 跳转登录页 |
| 登录 | admin / 123456 | 进入首页仪表盘，右上角"管理员"徽章 |
| 学员视角 | zhangsan / 123456 | 无"新增/编辑/删除"按钮，只见自己的借用记录；因有逾期记录，借用会被拒绝 |
| 正常借用 | wangwu / 123456（或注册新账号） | 借用成功后设备状态变"借出"，归还后恢复"在库" |

## 7. 常见故障分支

| 现象 | 原因 | 处理 |
| --- | --- | --- |
| 访问 `http://localhost:8080/labequip/` 404 或空白 | Tomcat 端口不是 8080 / war 未部署成功 | 确认 `conf/server.xml` HTTP 端口；查 catalina 日志有无部署异常 |
| 首页 404（`no Action mapped`） | 旧版 web.xml 缺 FORWARD dispatcher（欢迎页 `<jsp:forward>` 绕过 Struts） | 升级到最新代码（web.xml 已声明 REQUEST+FORWARD） |
| 登录后报"登录查询失败/Communications link failure" | 数据库未启动、库名不对、口令不对 | 先用 `mysql -u root -p` 确认可连；核对第 4 节环境变量 |
| `Public Key Retrieval is not allowed` | MySQL 8 caching_sha2_password 且未带公钥参数 | 连接串需含 `allowPublicKeyRetrieval=true`（默认值已含） |
| 中文乱码 | 数据库字符集非 utf8mb4 或绕过编码过滤器 | 重建库用 utf8mb4；确认 web.xml 中 encodingFilter 在 struts2 之前 |
| 图片上传失败/413 | 超过 5MB 限制 | `struts.multipart.maxSize` 与 Action 校验均为 5MB，换小图 |
| 8080 被占用 | 其他 Tomcat/应用占用 | 改 `server.xml` 端口，或停用占用进程；同时注意 shutdown 端口 8005 冲突 |

## 8. 目录速览

```
src/main/java/com/labequip/
  action/       BaseAction / Login / Register / Dashboard / Equipment / Borrow
  dao/          UserDao / EquipmentDao / BorrowRecordDao（事务借用/归还）
  entity/       User(role) / Equipment(image) / BorrowRecord
  interceptor/  LoginInterceptor（登录校验）
  servlet/      HelloServlet（实验一）/ HealthServlet（健康检查）
  util/         DBUtil（环境变量配置）/ DateUtil / EncodingFilter
src/main/resources/struts.xml      动作映射、上传上限
src/main/webapp/                   JSP、静态资源、web.xml
db/init.sql                        全量初始化
db/upgrade-v1.1.sql                旧库增量升级
```
