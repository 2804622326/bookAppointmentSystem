#!/bin/bash

# AWS ECS 部署脚本
set -e

# 配置变量
AWS_REGION="eu-west-1"
AWS_ACCOUNT_ID="614441038924"
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

FRONTEND_REPO_NAME="pet-care-frontend"
BACKEND_REPO_NAME="pet-care-backend"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

# 检查依赖
check_dependencies() {
    log "检查依赖..."
    
    if ! command -v aws &> /dev/null; then
        error "AWS CLI 未安装"
    fi
    
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装"
    fi
    
    # 检查 AWS 凭证
    if ! aws sts get-caller-identity &> /dev/null; then
        error "AWS 凭证未配置，请运行: aws configure"
    fi
    
    log "依赖检查完成 ✓"
}

# 登录 ECR
ecr_login() {
    log "登录到 ECR..."
    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
    log "ECR 登录成功 ✓"
}

# 创建 ECR 仓库
create_ecr_repos() {
    log "创建 ECR 仓库..."
    
    # 前端仓库
    if ! aws ecr describe-repositories --repository-names ${FRONTEND_REPO_NAME} --region ${AWS_REGION} &> /dev/null; then
        aws ecr create-repository --repository-name ${FRONTEND_REPO_NAME} --region ${AWS_REGION}
        log "创建前端仓库: ${FRONTEND_REPO_NAME} ✓"
    else
        log "前端仓库已存在: ${FRONTEND_REPO_NAME} ✓"
    fi
    
    # 后端仓库
    if ! aws ecr describe-repositories --repository-names ${BACKEND_REPO_NAME} --region ${AWS_REGION} &> /dev/null; then
        aws ecr create-repository --repository-name ${BACKEND_REPO_NAME} --region ${AWS_REGION}
        log "创建后端仓库: ${BACKEND_REPO_NAME} ✓"
    else
        log "后端仓库已存在: ${BACKEND_REPO_NAME} ✓"
    fi
}

# 构建并推送镜像
build_and_push_images() {
    log "构建并推送 Docker 镜像..."
    
    # 构建前端镜像
    log "构建前端镜像..."
    cd /workspaces/bookAppointmentSystem
    docker build -t ${FRONTEND_REPO_NAME}:latest ./frontend
    docker tag ${FRONTEND_REPO_NAME}:latest ${ECR_REGISTRY}/${FRONTEND_REPO_NAME}:latest
    docker push ${ECR_REGISTRY}/${FRONTEND_REPO_NAME}:latest
    log "前端镜像推送完成 ✓"
    
    # 构建后端镜像
    log "构建后端镜像..."
    docker build -t ${BACKEND_REPO_NAME}:latest ./backend
    docker tag ${BACKEND_REPO_NAME}:latest ${ECR_REGISTRY}/${BACKEND_REPO_NAME}:latest
    docker push ${ECR_REGISTRY}/${BACKEND_REPO_NAME}:latest
    log "后端镜像推送完成 ✓"
}

# 创建 CloudWatch 日志组
create_log_groups() {
    log "创建 CloudWatch 日志组..."
    
    aws logs create-log-group --log-group-name "/ecs/pet-care-frontend" --region ${AWS_REGION} 2>/dev/null || true
    aws logs create-log-group --log-group-name "/ecs/pet-care-backend" --region ${AWS_REGION} 2>/dev/null || true
    
    log "日志组创建完成 ✓"
}

# 部署 ECS 服务
deploy_ecs_services() {
    log "部署 ECS 服务..."
    
    # 检查是否存在 ECS 集群
    if ! aws ecs describe-clusters --clusters pet-care-cluster --region ${AWS_REGION} &> /dev/null; then
        warn "ECS 集群 'pet-care-cluster' 不存在，请先在 AWS 控制台创建"
        log "你可以运行以下命令创建集群："
        echo "aws ecs create-cluster --cluster-name pet-care-cluster --region ${AWS_REGION}"
        return 1
    fi
    
    log "更新 ECS 任务定义..."
    
    # 注册后端任务定义
    aws ecs register-task-definition \
        --cli-input-json file:///workspaces/bookAppointmentSystem/deploy/backend-task-definition.json \
        --region ${AWS_REGION}
    
    # 注册前端任务定义
    aws ecs register-task-definition \
        --cli-input-json file:///workspaces/bookAppointmentSystem/deploy/frontend-task-definition.json \
        --region ${AWS_REGION}
    
    log "ECS 任务定义更新完成 ✓"
}

# 主执行函数
main() {
    log "开始 AWS ECS 部署流程..."
    
    check_dependencies
    ecr_login
    create_ecr_repos
    create_log_groups
    build_and_push_images
    deploy_ecs_services
    
    log "部署完成! 🎉"
    log "后续步骤:"
    log "1. 在 AWS 控制台创建 RDS MySQL 数据库"
    log "2. 在 ECS 控制台创建服务"
    log "3. 配置 Application Load Balancer"
    log "4. 设置域名和 SSL 证书"
}

# 执行主函数
main "$@"