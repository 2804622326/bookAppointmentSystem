#!/bin/bash

# 构建ARM64架构镜像的脚本
# 适用于AWS ECS ARM64 Fargate任务

set -e

# 检查AWS凭证
if ! aws sts get-caller-identity >/dev/null 2>&1; then
    echo "❌ 错误: AWS凭证未配置"
    echo "请先设置环境变量:"
    echo "export AWS_ACCESS_KEY_ID=your_access_key"
    echo "export AWS_SECRET_ACCESS_KEY=your_secret_key"
    echo "export AWS_DEFAULT_REGION=eu-west-1"
    exit 1
fi

# 配置变量
AWS_REGION="eu-west-1"
AWS_ACCOUNT_ID="614441038924"
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
BACKEND_REPO="pet-care-backend"
FRONTEND_REPO="pet-care-frontend"
IMAGE_TAG="arm64-fix-$(date +%Y%m%d-%H%M%S)"

echo "🔧 构建ARM64架构镜像..."
echo "镜像标签: ${IMAGE_TAG}"

# 登录ECR
echo "🔐 登录ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# 创建/使用buildx构建器
echo "🛠️ 设置Docker buildx..."
docker buildx create --use --name arm64-builder --driver-opt network=host || docker buildx use arm64-builder
docker buildx inspect --bootstrap

# 构建后端镜像（ARM64架构）
echo "🔨 构建后端镜像 (ARM64)..."
docker buildx build \
  --platform linux/arm64 \
  --provenance=false \
  --sbom=false \
  -t $ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG \
  -t $ECR_REGISTRY/$BACKEND_REPO:arm64-latest \
  ./backend \
  --push

# 验证后端镜像
echo "🔍 验证后端镜像..."
docker buildx imagetools inspect $ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG

# 构建前端镜像（ARM64架构）
echo "🎨 构建前端镜像 (ARM64)..."
docker buildx build \
  --platform linux/arm64 \
  --provenance=false \
  --sbom=false \
  -t $ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG \
  -t $ECR_REGISTRY/$FRONTEND_REPO:arm64-latest \
  ./frontend \
  --push

# 验证前端镜像
echo "🔍 验证前端镜像..."
docker buildx imagetools inspect $ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG

echo "✅ ARM64镜像构建完成！"
echo "后端镜像: $ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG"
echo "前端镜像: $ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG"

echo ""
echo "📋 下一步操作："
echo "1. 在AWS控制台中更新ECS任务定义，使用以下镜像:"
echo "   后端: $ECR_REGISTRY/$BACKEND_REPO:arm64-latest"
echo "   前端: $ECR_REGISTRY/$FRONTEND_REPO:arm64-latest"
echo "2. 确保任务定义中的架构设置为 'ARM64'"
echo "3. 更新ECS服务以使用新的任务定义"

# 尝试更新服务（如果有权限的话）
echo ""
echo "🚀 尝试更新ECS服务..."
echo "注意: 如果遇到权限错误，请通过AWS控制台手动更新"

# 注意：这里我们不指定任务定义版本，让ECS使用当前的
aws ecs update-service \
    --cluster pet-care-cluster \
    --service pet-care-backend-service \
    --force-new-deployment \
    --region $AWS_REGION || echo "⚠️ 服务更新失败，请通过AWS控制台手动更新"

aws ecs update-service \
    --cluster pet-care-cluster \
    --service pet-care-frontend-service-jqsbfks7 \
    --force-new-deployment \
    --region $AWS_REGION || echo "⚠️ 服务更新失败，请通过AWS控制台手动更新"

echo "✅ 脚本执行完成"