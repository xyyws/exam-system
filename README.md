# 在线考试系统

基于前后端分离架构的在线考试管理系统，覆盖题库管理、在线组卷、考试组织、自动阅卷、成绩分析全流程。

## 技术栈

| 层次 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 前端框架 | Vue 3 | 3.5.13 | 渐进式 JavaScript 框架 |
| UI 组件库 | Element Plus | 2.9.8 | Vue 3 组件库 |
| 状态管理 | Pinia | 3.0.2 | Vue 官方状态管理 |
| 路由 | Vue Router | 4.5.1 | 官方路由管理器 |
| 构建工具 | Vite | 6.3.5 | 下一代前端构建工具 |
| 图表库 | ECharts | 6.0.0 | 数据可视化 |
| HTTP 客户端 | Axios | 1.8.4 | Promise based HTTP client |
| 后端框架 | Spring Boot | 3.3.6 | Java 企业级框架 |
| 安全框架 | Spring Security | 6.x | 认证授权 |
| ORM | MyBatis | 3.0.4 | SQL 映射框架 |
| 数据库 | MySQL | 8.x | 关系型数据库 |
| 缓存 | Redis | 6.x | 内存数据库 |
| JWT | jjwt | 0.12.6 | JWT 令牌库 |
| Excel 处理 | Apache POI | 5.2.5 | Office 文档处理 |
| 工具库 | Hutool | 5.8.35 | Java 工具集 |

## 项目结构

```
考试系统/
├── backend/                          # 后端 Spring Boot 项目
│   ├── src/main/java/com/exam/
│   │   ├── OnlineExamApplication.java
│   │   ├── auth/                     # 认证模块（登录、Token、密码）
│   │   ├── system/                   # 系统管理（用户、角色、班级、日志）
│   │   ├── question/                 # 题库管理（题目、分类、导入）
│   │   ├── paper/                    # 试卷管理（手动/自动组卷）
│   │   ├── exam/                     # 考试管理（创建、发布、监控）
│   │   ├── runtime/                  # 考试运行（答题、交卷、评分）
│   │   ├── marking/                  # 阅卷模块（主观题评分）
│   │   ├── analytics/                # 统计分析（错题本、成绩分析）
│   │   ├── common/                   # 公共模块（安全、异常、工具）
│   │   └── config/                   # 配置类（Security、Redis）
│   └── src/main/resources/
│       ├── application.yml           # 应用配置
│       └── mapper/                   # MyBatis XML 映射文件
├── frontend/                         # 前端 Vue 3 项目
│   └── src/
│       ├── api/                      # API 请求封装
│       ├── components/               # 公共组件
│       ├── layouts/                  # 布局组件
│       ├── router/                   # 路由配置
│       ├── stores/                   # Pinia 状态管理
│       ├── styles/                   # 全局样式
│       └── views/                    # 页面视图
│           ├── admin/                # 管理员页面
│           ├── teacher/              # 教师页面
│           └── student/              # 学生页面
├── sql/                              # 数据库脚本
│   ├── exam_system_init.sql          # 建表脚本
│   ├── seed_data.sql                 # 种子数据
│   └── migrations/                   # 数据迁移脚本
└── docs/                             # 项目文档
    ├── 1-需求规格说明书.md
    ├── 2-系统设计文档.md
    ├── 3-测试报告.md
    ├── 4-用户手册.md
    ├── 5-项目总结报告.md
    └── 6-项目统计报告.md
```

## 功能特性

### 管理员 (ADMIN)

- **数据统计仪表盘** — 系统概览、用户增长趋势、考试参与统计
- **学生管理** — 增删改查、搜索筛选、启用/禁用、重置密码
- **教师管理** — 增删改查、工号管理
- **班级管理** — 班级 CRUD、查看班级学生
- **系统日志** — 操作审计、按类型/时间/操作人筛选

### 教师 (TEACHER)

- **题库管理** — 五种题型（单选/多选/判断/填空/简答），支持难度、分类、知识点
- **批量导入** — Excel 文件批量导入题目
- **题目分类** — 树形分类结构管理
- **手动组卷** — 从题库选题、设置分值、调整顺序
- **自动组卷** — 按题型/难度/分类规则自动抽题
- **考试管理** — 创建考试、设置时间、配置防作弊策略、发布/结束考试
- **考试监控** — 实时查看考试状态、切屏违规记录
- **在线阅卷** — 主观题评分、填写评语
- **统计分析** — 成绩分布、题目分析

### 学生 (STUDENT)

- **我的考试** — 查看分配的考试列表
- **在线答题** — 答题卡、题目跳转、标记题目、自动保存、倒计时
- **防作弊** — 切屏检测、超限自动交卷
- **考试记录** — 历史考试记录
- **我的成绩** — 成绩列表、得分率趋势图表
- **成绩分析** — 各题型得分情况可视化
- **错题本** — 自动收录错题、标记已掌握、添加备注
- **个人中心** — 查看/修改个人信息

## 数据库设计

系统共 12 张核心业务表：

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_user_role | 用户角色关联表 |
| sys_class | 班级表 |
| question_category | 题目分类表 |
| question | 题目表 |
| question_option | 题目选项表 |
| paper | 试卷表 |
| paper_question | 试卷题目关联表 |
| exam | 考试表 |
| exam_paper | 答卷表 |
| exam_answer | 答题记录表 |
| wrong_question_book | 错题本表 |
| operation_log | 操作日志表 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x
- Redis 6.x
- Node.js 18+

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE online_exam DEFAULT CHARACTER SET utf8mb4;"

# 导入建表脚本
mysql -u root -p online_exam < sql/exam_system_init.sql

# 导入种子数据（含测试账号和示例数据）
mysql -u root -p online_exam < sql/seed_data.sql
```

### 2. 启动后端

```bash
cd backend

# 修改数据库连接（如需要）
# vim src/main/resources/application.yml

# 启动
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端默认运行在 `http://localhost:5173`。

### 4. 访问系统

打开浏览器访问 `http://localhost:5173`，使用以下测试账号登录：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher1 | 123456 |
| 学生 | student1 | 123456 |

## 安全设计

- **认证**：JWT 双 Token（Access Token 2h + Refresh Token 14d），Redis 存储支持主动失效
- **授权**：RBAC 角色权限，URL 级别隔离（`/api/admin/**`、`/api/teacher/**`、`/api/student/**`）
- **密码**：BCrypt 加密存储
- **SQL 注入**：MyBatis 参数化查询
- **防作弊**：切屏检测、题序/选项随机、IP/设备记录、超限自动交卷
- **审计**：AOP 切面自动记录关键操作日志

## API 概览

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1715980800000,
  "traceId": "abc123"
}
```

| 模块 | 路径前缀 | 接口数 |
|------|---------|--------|
| 认证 | `/api/auth` | 5 |
| 系统管理 | `/api/admin` | 14 |
| 题库管理 | `/api/teacher/questions` | 9 |
| 试卷管理 | `/api/teacher/papers` | 4 |
| 考试管理 | `/api/teacher/exams` | 6 |
| 阅卷 | `/api/teacher/marking` | 3 |
| 学生考试 | `/api/student` | 11 |
| 统计分析 | `/api/analytics` | 4 |

## 项目统计

| 指标 | 数值 |
|------|------|
| 后端 Java 文件 | 80+ |
| 前端 Vue 文件 | 40+ |
| 代码总行数 | 14700+ |
| API 接口 | 60+ |
| 数据库表 | 12 |
| 前端页面 | 30+ |
| 支持题型 | 5 |
| 用户角色 | 3 |
