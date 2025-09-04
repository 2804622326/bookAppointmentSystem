# 宠物护理预约系统 (Pet Care Appointment System)

这是一个全栈的宠物医疗预约管理系统，为宠物主人和兽医提供便捷的在线预约服务。

A full-stack pet care appointment management system that provides convenient online appointment services for pet owners and veterinarians.

## 🏥 系统特性 (System Features)

- **用户管理**: 宠物主人、兽医、管理员多角色支持
- **预约系统**: 在线预约、状态管理、邮件通知
- **宠物档案**: 宠物信息管理、医疗记录
- **评价系统**: 服务评分和反馈机制
- **数据统计**: 预约趋势、收入分析等数据可视化
- **安全认证**: JWT令牌认证、角色权限控制

## 🛠️ 技术栈 (Tech Stack)

### 后端 (Backend)
- **框架**: Spring Boot 3.3.0 (Java 17)
- **数据库**: MySQL 8.0
- **认证**: Spring Security + JWT
- **数据访问**: Spring Data JPA
- **邮件服务**: Spring Mail

### 前端 (Frontend)
- **框架**: React 18 + Vite
- **UI库**: Bootstrap 5 + React Bootstrap
- **状态管理**: React Hooks
- **HTTP客户端**: Axios
- **图表**: Recharts

### 部署 (Deployment)
- **容器化**: Docker + Docker Compose
- **反向代理**: Nginx
- **数据库**: MySQL 8.0

## 📋 快速开始 (Quick Start)

### 环境要求 (Requirements)
- Docker 20.10+
- Docker Compose 2.0+

### 使用Docker运行 (Run with Docker)
```bash
# 克隆项目
git clone https://github.com/2804622326/bookAppointmentSystem.git
cd bookAppointmentSystem

# 构建并启动所有服务
docker compose up --build

# 后台运行
docker compose up -d --build
```

### 访问应用 (Access Applications)
- **前端 (Frontend)**: [http://localhost:3000](http://localhost:3000)
- **后端API (Backend API)**: [http://localhost:9192](http://localhost:9192)
- **数据库 (Database)**: `localhost:3307` (用户名: root, 密码: Liminghao2001)

## 📚 文档 (Documentation)

- **[项目详细说明 (Project Overview)](./PROJECT_EXPLANATION_CN.md)** - 系统架构、功能特性详细介绍
- **[API 接口文档 (API Documentation)](./API_DOCUMENTATION_CN.md)** - RESTful API接口说明
- **[开发者指南 (Developer Guide)](./DEVELOPER_GUIDE_CN.md)** - 开发环境搭建、代码规范、测试指南

## 🏗️ 项目结构 (Project Structure)

```
bookAppointmentSystem/
├── backend/                 # Spring Boot 后端应用
│   ├── src/main/java/
│   │   └── com/dailycodework/universalpetcare/
│   │       ├── controller/  # REST 控制器
│   │       ├── service/     # 业务逻辑层
│   │       ├── model/       # 数据模型
│   │       ├── repository/  # 数据访问层
│   │       └── security/    # 安全配置
│   └── Dockerfile
├── frontend/               # React 前端应用
│   ├── src/
│   │   ├── components/     # React 组件
│   │   └── App.jsx
│   └── Dockerfile
├── docker-compose.yml      # Docker 编排配置
└── docs/                   # 项目文档
```

## 🚀 开发模式 (Development Mode)

### 后端开发 (Backend Development)
```bash
cd backend
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### 前端开发 (Frontend Development)
```bash
cd frontend
npm install
npm run dev
```

## 🧪 测试 (Testing)

### 后端测试 (Backend Tests)
```bash
cd backend
./mvnw test
```

### 前端测试 (Frontend Tests)
```bash
cd frontend
npm test
```

## 🔧 配置 (Configuration)

### 环境变量 (Environment Variables)
- `MYSQL_ROOT_PASSWORD`: 数据库root密码
- `MYSQL_DATABASE`: 数据库名称
- `JWT_SECRET`: JWT密钥
- `SPRING_PROFILES_ACTIVE`: Spring配置文件

### 数据库配置 (Database Configuration)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/pet-care-system
    username: root
    password: Liminghao2001
```

## 🤝 贡献 (Contributing)

1. Fork 这个项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证 (License)

这个项目使用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式 (Contact)

- 项目维护者: [2804622326](https://github.com/2804622326)
- 项目链接: [https://github.com/2804622326/bookAppointmentSystem](https://github.com/2804622326/bookAppointmentSystem)

## 🙏 致谢 (Acknowledgments)

- Spring Boot 团队提供的优秀框架
- React 团队的前端框架
- Bootstrap 的UI组件库
- 所有贡献者的努力

---

**注意**: 这是一个学习和演示项目，如需用于生产环境，请确保进行适当的安全配置和性能优化。
