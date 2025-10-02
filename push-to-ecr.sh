#!/bin/bash

# ECR 推送配置
ACCOUNT_ID=614441038924
REGION=us-east-1
ECR_URI=$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com
BACKEND_REPO=pet-care-backend
FRONTEND_REPO=pet-care-frontend

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

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

# 检查 Docker 是否运行
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        error "Docker 未运行，请启动 Docker"
    fi
    log "Docker 检查通过 ✓"
}

# 检查 AWS CLI 和凭证
check_aws() {
    if ! command -v aws &> /dev/null; then
        error "AWS CLI 未安装"
    fi
    
    if ! aws sts get-caller-identity &> /dev/null; then
        error "AWS 凭证未配置，请运行: aws configure"
    fi
    
    log "AWS CLI 检查通过 ✓"
}

# 登录 ECR
ecr_login() {
    log "登录到 ECR..."
    aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_URI
    if [ $? -eq 0 ]; then
        log "ECR 登录成功 ✓"
    else
        error "ECR 登录失败"
    fi
}

# 构建后端镜像
build_backend() {
    log "构建后端镜像..."
    cd /workspaces/bookAppointmentSystem
    
    docker build -t $BACKEND_REPO:latest ./backend
    if [ $? -eq 0 ]; then
        log "后端镜像构建成功 ✓"
    else
        error "后端镜像构建失败"
    fi
    
    # 标记镜像
    docker tag $BACKEND_REPO:latest $ECR_URI/$BACKEND_REPO:latest
    log "后端镜像标记完成 ✓"
}

# 构建前端镜像
build_frontend() {
    log "构建前端镜像..."
    cd /workspaces/bookAppointmentSystem
    
    docker build -t $FRONTEND_REPO:latest ./frontend
    if [ $? -eq 0 ]; then
        log "前端镜像构建成功 ✓"
    else
        error "前端镜像构建失败"
    fi
    
    # 标记镜像
    docker tag $FRONTEND_REPO:latest $ECR_URI/$FRONTEND_REPO:latest
    log "前端镜像标记完成 ✓"
}

# 推送后端镜像
push_backend() {
    log "推送后端镜像到 ECR..."
    docker push $ECR_URI/$BACKEND_REPO:latest
    if [ $? -eq 0 ]; then
        log "后端镜像推送成功 ✓"
    else
        error "后端镜像推送失败"
    fi
}

# 推送前端镜像
push_frontend() {
    log "推送前端镜像到 ECR..."
    docker push $ECR_URI/$FRONTEND_REPO:latest
    if [ $? -eq 0 ]; then
        log "前端镜像推送成功 ✓"
    else
        error "前端镜像推送失败"
    fi
}

# 显示推送结果
show_results() {
    echo
    echo -e "${GREEN}🎉 镜像推送完成!${NC}"
    echo -e "${GREEN}================================${NC}"
    echo "后端镜像: $ECR_URI/$BACKEND_REPO:latest"
    echo "前端镜像: $ECR_URI/$FRONTEND_REPO:latest"
    echo -e "${GREEN}================================${NC}"
    echo
    echo "验证推送结果:"
    echo "aws ecr describe-images --repository-name $BACKEND_REPO --region $REGION"
    echo "aws ecr describe-images --repository-name $FRONTEND_REPO --region $REGION"
}

# 主函数
main() {
    log "开始推送 Docker 镜像到 ECR..."
    
    check_docker
    check_aws
    ecr_login
    
    log "构建镜像..."
    build_backend
    build_frontend
    
    log "推送镜像..."
    push_backend
    push_frontend
    
    show_results
    
    log "全部完成! 🚀"
}

# 执行主函数
main "$@"