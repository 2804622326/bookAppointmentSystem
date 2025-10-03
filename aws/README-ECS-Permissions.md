# ECS 权限配置指南

## 问题描述
当前 `ecr-user` 缺少 ECS 相关权限，导致 Jenkins 流水线无法执行以下操作：
- `ecs:DescribeServices` - 查看服务状态
- `ecs:ListTasks` - 列出任务
- `ecs:DescribeTasks` - 查看任务详情

## 解决方案

### 1. 应用 ECS 权限策略

使用提供的 `ecs-permissions-policy.json` 文件为 `ecr-user` 添加权限：

```bash
# 创建 ECS 权限策略
aws iam create-policy \
  --policy-name ECSDeploymentPolicy \
  --policy-document file://aws/ecs-permissions-policy.json

# 附加策略到 ecr-user
aws iam attach-user-policy \
  --user-name ecr-user \
  --policy-arn arn:aws:iam::614441038924:policy/ECSDeploymentPolicy
```

### 2. 验证权限

测试权限是否生效：

```bash
# 测试 ECS 服务描述权限
aws ecs describe-services \
  --cluster pet-care-cluster \
  --services pet-care-backend-service \
  --region eu-west-1

# 测试任务列表权限
aws ecs list-tasks \
  --cluster pet-care-cluster \
  --service-name pet-care-backend-service \
  --region eu-west-1
```

## Jenkins 流水线修改

目前的修改：
- ✅ 移除了需要 ECS 权限的诊断命令
- ✅ 使用简单的时间等待策略
- ✅ 依赖健康检查阶段验证部署成功

添加权限后可选的增强功能：
- 恢复详细的服务状态监控
- 添加失败任务的诊断信息
- 实现智能等待策略

## 权限范围

策略包含的权限：
1. **ECS 服务管理** - 更新和查看指定服务
2. **ECS 集群访问** - 访问 pet-care-cluster
3. **负载均衡器** - 检查目标组健康状态

所有权限都限定在特定资源范围内，遵循最小权限原则。