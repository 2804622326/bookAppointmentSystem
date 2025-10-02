# AWS ECS 部署指南

本指南将帮你将 Pet Care System 部署到 AWS ECS (Elastic Container Service)。

## 前提条件

1. **AWS CLI 已安装并配置**
   ```bash
   aws configure
   # 输入你的 AWS Access Key ID, Secret Access Key, 区域 (eu-west-1)
   ```

2. **Docker 已安装**
   ```bash
   docker --version
   ```

3. **必要的 AWS 权限**
   - ECR (Elastic Container Registry)
   - ECS (Elastic Container Service)
   - RDS (Relational Database Service)
   - VPC, EC2, IAM
   - CloudFormation
   - Secrets Manager

## 部署步骤

### 第一步：配置环境变量

编辑 `deploy/.env` 文件，更新你的配置：

```bash
# 更新域名
DOMAIN_NAME=your-actual-domain.com
API_DOMAIN=api.your-actual-domain.com

# 其他配置已预设，通常不需要修改
```

### 第二步：部署基础设施

使用 CloudFormation 部署网络和数据库基础设施：

```bash
# 部署 CloudFormation 堆栈
aws cloudformation create-stack \
  --stack-name pet-care-infrastructure \
  --template-body file://deploy/infrastructure.yaml \
  --parameters \
    ParameterKey=VpcId,ParameterValue=vpc-xxxxxxxxx \
    ParameterKey=PublicSubnet1,ParameterValue=subnet-xxxxxxxxx \
    ParameterKey=PublicSubnet2,ParameterValue=subnet-yyyyyyyyy \
    ParameterKey=PrivateSubnet1,ParameterValue=subnet-zzzzzzzzz \
    ParameterKey=PrivateSubnet2,ParameterValue=subnet-aaaaaaaaa \
    ParameterKey=DBPassword,ParameterValue=YourSecurePassword123! \
    ParameterKey=DomainName,ParameterValue=your-domain.com \
  --capabilities CAPABILITY_NAMED_IAM \
  --region eu-west-1

# 等待堆栈创建完成
aws cloudformation wait stack-create-complete --stack-name pet-care-infrastructure --region eu-west-1
```

### 第三步：构建和推送 Docker 镜像

```bash
# 运行自动化部署脚本
./deploy/aws-deploy.sh
```

这个脚本将：
- 登录 ECR
- 创建 ECR 仓库
- 构建前端和后端 Docker 镜像
- 推送镜像到 ECR
- 创建 ECS 任务定义

### 第四步：创建 ECS 服务

```bash
# 创建 ECS 服务
./deploy/create-ecs-services.sh
```

这个脚本将：
- 创建 ECS 服务
- 配置负载均衡器
- 等待服务稳定运行

### 第五步：配置域名和 SSL

1. **获取负载均衡器 DNS 名称**：
   ```bash
   aws cloudformation describe-stacks \
     --stack-name pet-care-infrastructure \
     --query 'Stacks[0].Outputs[?OutputKey==`ALBDNSName`].OutputValue' \
     --output text --region eu-west-1
   ```

2. **配置 DNS 记录**：
   - 在你的域名提供商处创建 CNAME 记录
   - 将 `your-domain.com` 和 `api.your-domain.com` 指向 ALB DNS 名称

3. **配置 SSL 证书**：
   - 在 AWS Certificate Manager 申请 SSL 证书
   - 将证书绑定到 ALB HTTPS 监听器

## 验证部署

### 检查服务状态

```bash
# 检查 ECS 服务
aws ecs describe-services \
  --cluster pet-care-cluster \
  --services pet-care-frontend-service pet-care-backend-service \
  --region eu-west-1

# 检查任务运行状态
aws ecs list-tasks \
  --cluster pet-care-cluster \
  --region eu-west-1
```

### 测试 API

```bash
# 获取 ALB DNS 名称
ALB_DNS=$(aws cloudformation describe-stacks \
  --stack-name pet-care-infrastructure \
  --query 'Stacks[0].Outputs[?OutputKey==`ALBDNSName`].OutputValue' \
  --output text --region eu-west-1)

# 测试后端 API
curl http://${ALB_DNS}/api/v1/pets/get-types

# 测试前端
curl http://${ALB_DNS}/
```

## 更新部署

### 更新应用代码

```bash
# 1. 更新代码后，重新构建和推送镜像
./deploy/aws-deploy.sh

# 2. 强制服务重新部署
aws ecs update-service \
  --cluster pet-care-cluster \
  --service pet-care-backend-service \
  --force-new-deployment \
  --region eu-west-1

aws ecs update-service \
  --cluster pet-care-cluster \
  --service pet-care-frontend-service \
  --force-new-deployment \
  --region eu-west-1
```

### 更新环境变量

1. 编辑任务定义 JSON 文件
2. 重新注册任务定义
3. 更新服务使用新的任务定义

## 监控和日志

### CloudWatch 日志

```bash
# 查看后端日志
aws logs describe-log-streams \
  --log-group-name /ecs/pet-care-backend \
  --region eu-west-1

# 查看前端日志
aws logs describe-log-streams \
  --log-group-name /ecs/pet-care-frontend \
  --region eu-west-1
```

### 监控指标

在 CloudWatch 控制台查看以下指标：
- ECS 服务 CPU 和内存使用率
- ALB 请求数和延迟
- RDS 数据库连接和性能

## 故障排查

### 常见问题

1. **容器启动失败**
   - 检查 CloudWatch 日志
   - 验证环境变量配置
   - 确认镜像构建正确

2. **数据库连接问题**
   - 检查 RDS 实例状态
   - 验证安全组配置
   - 确认数据库凭据

3. **负载均衡器健康检查失败**
   - 检查容器端口映射
   - 验证健康检查路径
   - 查看目标组健康状态

### 清理资源

```bash
# 删除 ECS 服务
aws ecs delete-service \
  --cluster pet-care-cluster \
  --service pet-care-frontend-service \
  --force \
  --region eu-west-1

aws ecs delete-service \
  --cluster pet-care-cluster \
  --service pet-care-backend-service \
  --force \
  --region eu-west-1

# 删除 CloudFormation 堆栈
aws cloudformation delete-stack \
  --stack-name pet-care-infrastructure \
  --region eu-west-1
```

## 成本优化

1. **使用 Fargate Spot**：在任务定义中配置 FARGATE_SPOT
2. **自动扩缩容**：根据 CPU/内存使用率配置 Auto Scaling
3. **预留容量**：对于长期运行的服务考虑 Reserved Instances
4. **监控成本**：使用 AWS Cost Explorer 监控费用

## 安全最佳实践

1. **最小权限原则**：IAM 角色只授予必需权限
2. **网络隔离**：使用私有子网运行容器
3. **密码管理**：使用 Secrets Manager 存储敏感信息
4. **SSL/TLS**：强制使用 HTTPS
5. **安全组**：限制入站规则到最小范围

---

## 支持

如有问题，请检查：
1. AWS CloudFormation 事件
2. ECS 服务事件
3. CloudWatch 日志
4. ALB 访问日志