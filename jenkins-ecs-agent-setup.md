# Jenkins ECS Agent 配置指南

## 📋 当前配置状态
✅ ECS Agent Template已配置  
✅ Label: `ecs-agent`  
✅ Jenkinsfile已更新使用ECS agent

## 🔧 ECS Agent Template 配置检查

### 1. 基本配置验证
在 Jenkins → Manage Jenkins → Manage Nodes and Clouds → Configure Clouds → ECS-book-appointment 中检查：

**Agent Template配置：**
- Label: `ecs-agent`
- Task Definition Name: 建议使用 `jenkins-agent-task`
- Image: `jenkins/inbound-agent:latest`
- Memory: 建议至少 `2048` (2GB)
- CPU Units: 建议至少 `1024` (1 vCPU)

### 2. 网络配置
**VPC配置：**
- VPC: 选择与ECS集群相同的VPC
- Security Group: 确保允许Jenkins master访问
- Subnets: 选择private subnets（推荐）

**安全组规则：**
```
Inbound Rules:
- Type: Custom TCP
- Port: 50000 (Jenkins agent通信端口)
- Source: Jenkins master的安全组

Outbound Rules:
- Type: All traffic
- Destination: 0.0.0.0/0
```

### 3. IAM角色配置
**Task Execution Role权限：**
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecr:GetAuthorizationToken",
                "ecr:BatchCheckLayerAvailability",
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "ecs:*",
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": "*"
        }
    ]
}
```

**Task Role权限（用于pipeline执行）：**
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecr:*",
                "ecs:*",
                "iam:PassRole"
            ],
            "Resource": "*"
        }
    ]
}
```

## 🚀 Pipeline执行流程

### 1. Agent启动过程
1. Jenkins检测到使用`ecs-agent` label的job
2. 在ECS中启动新的task实例
3. Agent自动连接到Jenkins master
4. 执行pipeline stages
5. 完成后自动终止task

### 2. 资源管理
**优势：**
- 弹性扩缩容，按需启动agent
- 隔离的执行环境
- 不占用Jenkins master资源

**注意事项：**
- 首次启动时间较长（需要拉取镜像）
- 网络配置要正确
- IAM权限要充分

## 🔍 故障排查

### 1. Agent连接失败
```bash
# 检查安全组配置
aws ec2 describe-security-groups --group-ids sg-xxxxxxxxx

# 检查子网配置
aws ec2 describe-subnets --subnet-ids subnet-xxxxxxxxx

# 检查Jenkins master日志
tail -f /var/log/jenkins/jenkins.log
```

### 2. ECS Task启动失败
```bash
# 查看ECS服务日志
aws ecs describe-tasks --cluster your-cluster --tasks task-id

# 查看CloudWatch日志
aws logs describe-log-groups --log-group-name-prefix "/aws/ecs/"
```

### 3. Docker权限问题
在ECS Task Definition中添加：
```json
{
    "mountPoints": [
        {
            "sourceVolume": "docker-sock",
            "containerPath": "/var/run/docker.sock"
        }
    ],
    "volumes": [
        {
            "name": "docker-sock",
            "host": {
                "sourcePath": "/var/run/docker.sock"
            }
        }
    ]
}
```

## 📊 性能优化建议

### 1. 镜像优化
创建自定义Jenkins agent镜像，预装必要工具：
```dockerfile
FROM jenkins/inbound-agent:latest
USER root

# 安装Docker
RUN apt-get update && \
    apt-get install -y docker.io && \
    usermod -aG docker jenkins

# 安装AWS CLI
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
    unzip awscliv2.zip && \
    ./aws/install

# 安装其他工具
RUN apt-get install -y maven nodejs npm

USER jenkins
```

### 2. 缓存策略
在pipeline中添加缓存：
```groovy
stage('Cache Dependencies') {
    steps {
        // Maven缓存
        sh 'mkdir -p ~/.m2'
        
        // NPM缓存
        sh 'mkdir -p ~/.npm'
    }
}
```

### 3. 并行执行
利用ECS的弹性能力，配置多个并行stage：
```groovy
parallel {
    stage('Backend Build') { ... }
    stage('Frontend Build') { ... }
    stage('Security Scan') { ... }
}
```

## 🎯 下一步操作

1. **验证ECS Agent配置**：
   - 检查网络和安全组设置
   - 验证IAM角色权限

2. **运行测试Pipeline**：
   - 创建简单的测试job
   - 验证agent能正常启动和连接

3. **执行完整部署**：
   - 运行pet-care-system-pipeline
   - 监控执行过程和资源使用

4. **性能调优**：
   - 根据执行结果调整资源配置
   - 优化构建时间和成本