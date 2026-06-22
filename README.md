# 瑞吉外卖系统

一个功能完善的在线外卖订餐平台，支持用户端订餐和商家端店铺管理两大核心模块。

## 项目内容

### 技术架构

| 层次 | 技术栈 | 版本 |
|------|--------|------|
| 后端框架 | Spring Boot | 3.2.5 |
| 数据库 | MySQL | - |
| 缓存 | Redis | - |
| ORM框架 | JPA / Hibernate | - |
| 前端框架 | Vue | 3.5.34 |
| 构建工具 | Vite | 8.0.12 |
| 路由管理 | Vue Router | 5.1.0 |
| HTTP客户端 | Axios | 1.17.0 |

### 功能模块

#### 用户端功能
- **账户管理**：用户注册、登录、个人信息维护
- **商家浏览**：按分类筛选、关键词搜索商家列表
- **菜品浏览**：商家详情页查看菜单、评分、配送信息
- **购物车**：添加/删除菜品、数量修改、结算
- **订单管理**：下单、订单列表、订单状态跟踪、取消订单
- **地址管理**：新增、编辑、删除收货地址
- **评价系统**：对已完成的订单进行评分和评论

#### 商家端功能
- **账户管理**：商家注册、登录、店铺信息维护
- **店铺管理**：基本信息编辑、营业状态设置
- **菜品管理**：添加/编辑/删除菜品，分类管理
- **订单处理**：查看新订单、确认/拒绝订单、发货
- **评价管理**：查看用户评价和评分统计

## 项目的实用性

本系统可应用于以下场景：

| 场景 | 说明 |
|------|------|
| **学习参考** | Spring Boot + Vue 3 全栈开发的完整示例，适合作为毕业设计或课程项目 |
| **二次开发** | 基于本项目快速搭建外卖、电商、社区团购等类似业务系统 |
| **企业原型** | 可作为创业项目的技术原型，快速验证业务可行性 |
| **教学演示** | 包含完整的用户端和商家端，适合教学演示前后端交互流程 |

### 适用人群
- Java/Web 开发学习者
- 毕业设计/课程设计需求
- 创业团队快速原型验证
- 小型餐饮店数字化转型

## 用户如何开始项目

### 环境要求

| 环境 | 版本要求 |
|------|----------|
| JDK | 17 及以上 |
| Node.js | 16 及以上 |
| MySQL | 8.0 及以上 |
| Redis | 6.0 及以上 |
| Maven | 3.6 及以上 |

### 快速启动

#### 1. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE takeaway_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 修改 application.yml 中的数据库连接信息
spring:
  datasource:
    username: root          -- 修改为你的数据库用户名
    password: your_password -- 修改为你的数据库密码
```

#### 2. 启动后端服务

```bash
cd d:\rjwm\takeaway

# 编译并启动（开发模式）
mvn spring-boot:run

# 或者先编译打包再运行
mvn clean package
java -jar target/takeaway-1.0.0.jar
```

后端启动成功后会显示：
```
Started TakeawayApplication in x.xx seconds
Tomcat started on port 8080
```

#### 3. 启动前端服务

```bash
cd d:\rjwm\takeaway\frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后会显示：
```
VITE ready in xxx ms
➜  Local: http://localhost:5173/
```

#### 4. 访问系统

| 端 | 访问地址 |
|----|----------|
| 用户端 | http://localhost:5173 |
| 商家端 | http://localhost:5173/business-login |

### 默认测试账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 用户 | （注册后使用） | - | 通过注册页面创建 |
| 商家 | （注册后使用） | - | 通过商家注册页面创建 |

## 获得项目帮助

### 文档资料

| 资料 | 位置 | 说明 |
|------|------|------|
| 代码注释 | 各 Java/Vue 文件 | 包含详细的类和方法说明 |
| API 文档 | Controller 接口类 | 定义了所有 RESTful 接口 |
| 实体类文档 | Entity 目录 | 包含字段说明和数据约束 |
| 配置文件 | application.yml | 包含所有配置项注释 |

### 技术文档

| 技术 | 文档链接 |
|------|----------|
| Spring Boot | https://spring.io/projects/spring-boot |
| Vue 3 | https://vuejs.org/ |
| Vue Router | https://router.vuejs.org/ |
| JPA/Hibernate | https://hibernate.org/orm/documentation/ |
| MySQL | https://dev.mysql.com/doc/ |

### 常见问题排查

| 问题 | 解决方案 |
|------|----------|
| 端口被占用 | 使用 `netstat -ano \| findstr :8080` 检查并终止占用进程 |
| 数据库连接失败 | 确认 MySQL 服务运行中，检查用户名密码配置 |
| 前端无法请求后端 | 确认后端在 8080 端口运行，检查跨域配置 |
| Redis 连接失败 | 确认 Redis 服务运行中，检查 application.yml 配置 |

## 维护与贡献

### 项目维护

| 维护者 | 职责 |
|--------|------|
| 开发团队 | 项目功能开发、bug 修复、技术更新 |

### 如何贡献

欢迎通过以下方式参与项目贡献：

1. **功能建议**：提交 Issue 说明功能需求
2. **Bug 反馈**：提交 Issue 描述问题及复现步骤
3. **代码优化**：Fork 项目，提交 Pull Request
4. **文档完善**：帮助完善 README 或代码注释

### 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式（不影响功能）
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

### 联系方式

| 渠道 | 说明 |
|------|------|
| 项目仓库 | 当前 Git 仓库 |
| 问题反馈 | 通过 Issue 提交 |

### 许可证

本项目仅供学习和参考使用，未经授权不得用于商业用途。

---

**版本**：1.0.0  
**最后更新**：2026-06-22
