# Pet Care System - Jenkins CI/CD

## 📁 项目结构
```
bookAppointmentSystem/
├── Jenkinsfile              # Jenkins CI/CD 流水线配置
├── docker-compose.yml       # 本地开发环境
├── backend/
│   ├── Dockerfile          # 后端 Docker 镜像配置
│   └── ...                 # Spring Boot 应用代码
├── frontend/
│   ├── Dockerfile          # 前端 Docker 镜像配置  
│   └── ...                 # React 应用代码
└── mysql/                  # 数据库初始化脚本
```

## 🚀 Jenkins 配置

### 环境变量 (已配置)
- **AWS 区域**: `eu-west-1`
- **AWS 账户**: `614441038924`
- **ECR 仓库**:
  - Backend: `614441038924.dkr.ecr.eu-west-1.amazonaws.com/pet-care-backend`
  - Frontend: `614441038924.dkr.ecr.eu-west-1.amazonaws.com/pet-care-frontend`
- **ALB DNS**: `ALB-pet-384183543.eu-west-1.elb.amazonaws.com`
- **ECS 集群**: `pet-care-cluster`

### Jenkins 要求
1. **ECS Agent**: Label 为 `ecs-agent`
2. **AWS 凭证**: ID 为 `aws-credentials`
3. **工具配置**:
   - Maven: `Maven-3.9.0`
   - NodeJS: `NodeJS-18`

## 🔄 CI/CD 流程

1. **Checkout** - 检出代码
2. **Build & Test Backend** - Maven 构建和测试
3. **Build & Test Frontend** - npm 构建和测试
4. **Build Docker Images** - 构建容器镜像
5. **Security Scan** - Trivy 安全扫描
6. **Push to ECR** - 推送镜像到 AWS ECR
7. **Deploy to ECS** - 部署到 ECS 服务
8. **Health Check** - 应用健康检查

## 📝 部署说明

### 前提条件
- ECS 集群 `pet-care-cluster` 已创建
- ECS 服务 `pet-care-backend-service` 和 `pet-care-frontend-service` 已创建
- ECR 仓库已创建并可访问

### 手动触发部署
在 Jenkins 中运行 Pipeline 即可完成自动部署

### 应用访问
- **前端**: http://ALB-pet-384183543.eu-west-1.elb.amazonaws.com
- **API**: http://ALB-pet-384183543.eu-west-1.elb.amazonaws.com/api/v1