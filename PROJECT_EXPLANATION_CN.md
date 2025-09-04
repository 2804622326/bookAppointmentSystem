# 宠物护理预约系统项目说明

## 项目概述

这是一个全栈的宠物医疗预约管理系统，为宠物主人和兽医提供便捷的在线预约服务。系统采用现代化的技术栈，包含前端React应用、后端Spring Boot应用和MySQL数据库。

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.3.0 (Java 17)
- **数据库**: MySQL 8.0
- **认证**: JWT (JSON Web Token)
- **安全**: Spring Security
- **数据访问**: Spring Data JPA / Hibernate
- **邮件服务**: Spring Mail
- **API文档**: RESTful API
- **构建工具**: Maven
- **容器化**: Docker

### 前端技术栈
- **框架**: React 18
- **构建工具**: Vite
- **UI框架**: Bootstrap 5, React Bootstrap
- **路由**: React Router DOM
- **HTTP客户端**: Axios
- **图标**: React Icons
- **图表**: Recharts
- **日期选择**: React DatePicker
- **测试**: Jest, React Testing Library

## 系统功能特性

### 用户管理
- **用户注册**: 支持宠物主人和兽医注册
- **邮箱验证**: 新用户邮箱验证机制
- **用户登录**: JWT令牌认证
- **密码重置**: 邮箱密码重置功能
- **个人信息管理**: 用户可以更新个人资料

### 角色权限
- **宠物主人 (Patient)**: 可以预约、管理宠物信息、查看预约历史
- **兽医 (Veterinarian)**: 可以管理预约、查看患者信息、设置工作时间
- **管理员 (Admin)**: 系统管理、用户管理、数据统计

### 预约管理
- **在线预约**: 宠物主人可以选择兽医和时间进行预约
- **预约状态**: 待确认、已确认、已完成、已取消
- **预约历史**: 查看历史预约记录
- **预约通知**: 邮件通知功能

### 宠物管理
- **宠物信息**: 添加、编辑宠物基本信息
- **医疗记录**: 记录宠物的医疗历史
- **照片上传**: 支持宠物照片上传

### 兽医服务
- **专业信息**: 兽医可以设置专业领域
- **工作时间**: 设置可预约的时间段
- **服务评价**: 接收客户评价和反馈

### 评价系统
- **服务评价**: 宠物主人可以对兽医服务进行评价
- **评分系统**: 五星评分机制
- **评价展示**: 在兽医详情页面展示评价

### 数据统计
- **仪表板**: 管理员和兽医可以查看数据统计
- **图表展示**: 使用Recharts展示预约趋势、收入统计等
- **报表功能**: 生成各类业务报表

## 系统架构设计

### 数据库设计
主要实体包括：
- **用户 (User)**: 基础用户信息，使用继承策略
- **患者 (Patient)**: 继承自User，宠物主人
- **兽医 (Veterinarian)**: 继承自User，兽医信息
- **管理员 (Admin)**: 继承自User，管理员
- **宠物 (Pet)**: 宠物基本信息
- **预约 (Appointment)**: 预约记录
- **评价 (Review)**: 服务评价
- **角色 (Role)**: 用户角色权限
- **照片 (Photo)**: 图片存储

### API设计
RESTful API设计，主要控制器包括：
- `AuthController`: 认证相关API
- `UserController`: 用户管理API
- `AppointmentController`: 预约管理API
- `PatientController`: 患者相关API
- `VeterinarianController`: 兽医相关API
- `PetController`: 宠物管理API
- `ReviewController`: 评价管理API
- `AdminController`: 管理员功能API

### 前端组件架构
- **组件化设计**: 高度模块化的React组件
- **路由保护**: 基于角色的路由保护
- **状态管理**: 使用React Hooks管理状态
- **响应式设计**: Bootstrap响应式布局

## 部署方式

### Docker容器化部署
项目提供完整的Docker容器化解决方案：

```yaml
# docker-compose.yml 配置
services:
  db:           # MySQL数据库服务
    image: mysql:8
    ports: ["3307:3306"]
    
  backend:      # Spring Boot后端服务
    build: ./backend
    ports: ["9192:9192"]
    
  frontend:     # React前端服务
    build: ./frontend
    ports: ["3000:80"]
```

### 本地开发环境
1. 数据库：MySQL 8.0
2. 后端：Java 17 + Maven
3. 前端：Node.js + npm

## 安装和运行

### 使用Docker Compose（推荐）
```bash
# 克隆项目
git clone <repository-url>
cd bookAppointmentSystem

# 构建并启动所有服务
docker compose up --build

# 访问应用
# 前端: http://localhost:3000
# 后端API: http://localhost:9192
```

### 手动安装
#### 后端
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

#### 前端
```bash
cd frontend
npm install
npm run dev
```

## 项目目录结构

```
bookAppointmentSystem/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/
│   │   └── com/dailycodework/universalpetcare/
│   │       ├── controller/  # REST控制器
│   │       ├── service/     # 业务逻辑层
│   │       ├── model/       # 数据模型
│   │       ├── repository/  # 数据访问层
│   │       ├── security/    # 安全配置
│   │       ├── config/      # 配置类
│   │       └── dto/         # 数据传输对象
│   ├── pom.xml             # Maven配置
│   └── Dockerfile          # 后端Docker配置
├── frontend/               # React前端
│   ├── src/
│   │   ├── components/     # React组件
│   │   │   ├── auth/       # 认证组件
│   │   │   ├── appointment/# 预约组件
│   │   │   ├── user/       # 用户组件
│   │   │   ├── veterinarian/# 兽医组件
│   │   │   ├── admin/      # 管理员组件
│   │   │   └── common/     # 通用组件
│   │   ├── App.jsx         # 主应用组件
│   │   └── main.jsx        # 应用入口
│   ├── package.json        # 依赖配置
│   └── Dockerfile          # 前端Docker配置
├── docker-compose.yml      # Docker编排配置
└── README.md              # 项目说明
```

## 核心功能流程

### 用户注册流程
1. 用户填写注册信息（姓名、邮箱、密码、用户类型）
2. 系统发送验证邮件
3. 用户点击邮件链接验证账户
4. 账户激活，可以正常登录

### 预约流程
1. 宠物主人登录系统
2. 浏览兽医列表，查看兽医信息和评价
3. 选择兽医和可用时间槽
4. 填写预约信息（宠物、预约原因）
5. 提交预约申请
6. 兽医确认或拒绝预约
7. 系统发送邮件通知

### 兽医工作流程
1. 兽医登录系统
2. 查看预约请求列表
3. 确认或拒绝预约
4. 管理个人时间表
5. 查看患者和宠物信息
6. 完成预约并记录诊疗信息

## 安全特性

### 认证和授权
- JWT令牌认证机制
- 基于角色的访问控制 (RBAC)
- 密码加密存储
- 邮箱验证机制

### 数据安全
- SQL注入防护
- XSS攻击防护
- CSRF保护
- 敏感信息加密

## 扩展功能

### 当前可扩展的功能
- 在线支付集成
- 视频问诊功能
- 移动应用开发
- 多语言支持
- 短信通知服务
- 地理位置服务

### 性能优化
- 数据库查询优化
- 缓存机制 (Redis)
- CDN集成
- 图片压缩和优化

## 开发和维护

### 代码质量
- 统一的代码规范 (ESLint)
- 单元测试覆盖
- 集成测试
- API文档维护

### 监控和日志
- 应用性能监控
- 错误日志记录
- 用户行为分析
- 系统健康检查

## 技术亮点

1. **现代化技术栈**: 使用最新的Spring Boot 3和React 18
2. **容器化部署**: 完整的Docker容器化解决方案
3. **安全性**: 完善的认证授权和数据保护机制
4. **用户体验**: 响应式设计，良好的交互体验
5. **可扩展性**: 模块化设计，易于功能扩展
6. **代码质量**: 良好的项目结构和代码规范

这个宠物护理预约系统是一个功能完整、技术先进的现代化Web应用，适合作为学习全栈开发的优秀案例，也可以作为实际商业应用的基础进行定制开发。