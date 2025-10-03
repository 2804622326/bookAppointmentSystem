#!/bin/bash

# 手动构建和推送AMD64镜像的脚本
# 在出现架构问题时使用

set -e

# 配置变量
AWS_REGION="eu-west-1"
AWS_ACCOUNT_ID="614441038924"
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
BACKEND_REPO="pet-care-backend"
FRONTEND_REPO="pet-care-frontend"
IMAGE_TAG="manual-fix-$(date +%Y%m%d-%H%M%S)"

echo "🔧 手动构建AMD64架构镜像..."
echo "镜像标签: ${IMAGE_TAG}"

# 登录ECR
echo "🔐 登录ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# 创建/使用buildx构建器
echo "🛠️ 设置Docker buildx..."
docker buildx create --use --name amd64-builder --driver-opt network=host || docker buildx use amd64-builder

# 构建后端镜像
echo "🔨 构建后端镜像 (仅AMD64)..."
docker buildx build \
  --platform linux/amd64 \
  --provenance=false \
  --sbom=false \
  -t $ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG \
  -t $ECR_REGISTRY/$BACKEND_REPO:latest \
  ./backend \
  --push

# 验证后端镜像
echo "🔍 验证后端镜像..."
docker buildx imagetools inspect $ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG

# 构建前端镜像
echo "🎨 构建前端镜像 (仅AMD64)..."
docker buildx build \
  --platform linux/amd64 \
  --provenance=false \
  --sbom=false \
  -t $ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG \
  -t $ECR_REGISTRY/$FRONTEND_REPO:latest \
  ./frontend \
  --push

# 验证前端镜像
echo "🔍 验证前端镜像..."
docker buildx imagetools inspect $ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG

echo "✅ 镜像构建完成！"
echo "后端镜像: $ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG"
echo "前端镜像: $ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG"

# 更新ECS服务
echo "🚀 更新ECS服务..."
aws ecs update-service \
    --cluster pet-care-cluster \
    --service pet-care-backend-service \
    --force-new-deployment \
    --region $AWS_REGION

aws ecs update-service \
    --cluster pet-care-cluster \
    --service pet-care-frontend-service-jqsbfks7 \
    --force-new-deployment \
    --region $AWS_REGION

echo "✅ 部署更新已触发，请等待几分钟让服务重启"