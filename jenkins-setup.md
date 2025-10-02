# Jenkins CI/CD 配置指南

## 📋 Jenkins 环境准备

### 1. 必需插件
在 Jenkins 管理界面安装以下插件：
```
- Pipeline
- Docker Pipeline
- AWS Pipeline
- Git
- Blue Ocean (推荐)
- HTML Publisher (用于测试报告)
- Slack Notification (可选)
```

### 2. 全局工具配置
在 Jenkins → 全局工具配置中设置：

**Maven**
- 名称: `Maven-3.9.0`
- 版本: `3.9.0` 或最新版本

**NodeJS**
- 名称: `NodeJS-18`
- 版本: `18.x` 或最新 LTS

### 3. AWS 凭证配置
在 Jenkins → 凭据中添加：

**AWS 凭证**
- 类型: `AWS Credentials`
- ID: `aws-credentials`
- Access Key ID: 你的 AWS Access Key
- Secret Access Key: 你的 AWS Secret Key
- 描述: `AWS ECR/ECS 部署凭证`

## 🚀 创建 Jenkins Pipeline 任务

### 步骤：
1. **新建任务**
   - Jenkins 首页 → "新建任务"
   - 输入名称: `pet-care-system-pipeline`
   - 选择 "Pipeline"

2. **配置 Pipeline**
   - **Pipeline 定义**: `Pipeline script from SCM`
   - **SCM**: `Git`
   - **Repository URL**: `https://github.com/2804622326/bookAppointmentSystem.git`
   - **Credentials**: 添加 GitHub 凭证
   - **分支**: `*/codex/improve-instruction-coverage`
   - **Script Path**: `Jenkinsfile`

3. **构建触发器**（可选）
   - ✅ `GitHub hook trigger for GITScm polling`
   - ✅ `Poll SCM`: `H/5 * * * *` (每5分钟检查一次)

## ⚙️ 环境配置调整

### 1. 修改 ALB 地址
在 Jenkinsfile 中找到这行并替换为你的实际 ALB 地址：
```groovy
def albDns = "your-alb-dns-name.us-east-1.elb.amazonaws.com"
```

### 2. ECS 服务名称确认
确认以下服务名称与你在 AWS 中创建的一致：
```groovy
BACKEND_SERVICE = 'pet-care-backend-service'
FRONTEND_SERVICE = 'pet-care-frontend-service'
```

## 🔧 Jenkins 服务器配置

### 1. 安装 Docker
```bash
# 在 Jenkins 服务器上安装 Docker
sudo apt update
sudo apt install docker.io -y
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### 2. 安装 AWS CLI
```bash
# 在 Jenkins 服务器上安装 AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

### 3. 安装 Trivy (安全扫描)
```bash
# 安装 Trivy 进行镜像安全扫描
sudo apt-get update
sudo apt-get install wget apt-transport-https gnupg lsb-release
wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | sudo tee -a /etc/apt/sources.list.d/trivy.list
sudo apt-get update
sudo apt-get install trivy
```

## 📊 流水线功能说明

### 阶段说明：
1. **Checkout**: 检出最新代码
2. **Build & Test Backend**: Maven 构建和测试后端
3. **Build & Test Frontend**: npm 构建和测试前端
4. **Build Docker Images**: 并行构建前后端镜像
5. **Security Scan**: 使用 Trivy 扫描镜像漏洞
6. **Push to ECR**: 推送镜像到 ECR
7. **Deploy to ECS**: 更新 ECS 服务
8. **Wait for Deployment**: 等待部署完成
9. **Health Check**: 验证应用健康状态

### 特性：
- ✅ 并行构建提升效率
- ✅ 自动化测试和报告
- ✅ 安全漏洞扫描
- ✅ 滚动更新部署
- ✅ 健康检查验证
- ✅ 失败通知和回滚

## 🎯 使用流程

### 触发构建：
1. **手动触发**: Jenkins 界面点击 "立即构建"
2. **代码推送**: 推送到 `codex/improve-instruction-coverage` 分支
3. **定时构建**: 每5分钟检查代码变更

### 监控部署：
1. Jenkins Blue Ocean 界面查看流水线进度
2. AWS ECS 控制台查看服务更新状态
3. ALB 地址验证应用可用性

## 🔍 故障排查

### 常见问题：
1. **Docker 权限**: 确保 jenkins 用户在 docker 组
2. **AWS 凭证**: 验证 IAM 权限包含 ECR、ECS 操作
3. **网络连接**: 确保 Jenkins 服务器可以访问 AWS API
4. **镜像构建**: 检查 Dockerfile 和依赖项

---

**现在你可以按照这个指南配置 Jenkins，然后运行流水线进行自动化部署！** 🚀