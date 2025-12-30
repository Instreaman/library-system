# 图书借阅系统

中文说明 | [English](README.md)

一个简单的图书借阅系统，后端使用 Spring Boot + MyBatis-Plus + MariaDB，前端为静态 HTML + Axios，请求通过自定义拦截器完成 JWT 鉴权。前端用轻量 HTTP 服务启动。

## 项目结构
- `library/`：Spring Boot 后端
- `library-frontend/`：静态前端（HTML/JS/CSS）
- `database/init.sql`：数据库建表与初始化数据
- `Markdown/`：实训讲义

## 环境要求
- JDK 17+
- Maven
- MariaDB/MySQL
- Python 3（用于简易静态服务器）

## 快速开始
1) **初始化数据库**
```bash
mariadb -u <user> -p < /home/instreaman/Code/library-system/database/init.sql
```
默认数据库：`library_db`，默认凭据见 `application.yml`（用户 `library`，密码 `library123`）。

2) **启动后端**
```bash
cd library
mvn spring-boot:run
```
服务地址：http://localhost:8080

3) **启动前端**
```bash
cd library-frontend
python -m http.server 5500
```
访问：http://localhost:5500/pages/login.html（或从 index.html 自动跳转）。

## 默认账号
- 管理员：`admin` / `admin123`
- 普通用户：`user1` / `user123`

## 后端要点
- JWT 工具：`JwtUtils`
- 登录拦截器写入 `userId`、`username`、`role` 到 request attribute，控制器可直接获取。
- 借阅接口 `/api/user/**` 在事务内同步维护 `available_quantity` 与 `borrowed_quantity`。
- 数据表：`book`、`user`、`borrow_record`。

## 前端要点
- API 基础地址在 `library-frontend/js/config.js` 中设置为 `http://localhost:8080/api`。
- Token 存储 key：`library_token`（localStorage）。
- 用户页面：`pages/user/books.html`（我的借阅）与 `pages/user/borrow_book.html`（可借列表）。
- 管理员页面：`pages/admin/books.html`（图书 CRUD）与 `pages/admin/book_record.html`（借阅历史）。

## VS Code 任务
- 启动后端：`mvn spring-boot:run`（已在 `.vscode/tasks.json` 配置）
- 启动前端：`python -m http.server 5500`
- 停止任务：同上任务面板

## 测试清单
- 管理员登录，新增/编辑/删除图书（有未归还记录时删除会被阻止）。
- 普通用户登录，借阅图书后检查 `available_quantity` 减少、`borrowed_quantity` 增加。
- 归还图书后，库存与借出数量恢复。
- 浏览器 Network/Console 无 401 或 CORS 报错。

## 许可协议
MIT
