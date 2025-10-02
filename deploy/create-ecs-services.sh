#!/bin/bash

# ECS 服务创建和更新脚本
set -e

# 载入环境变量
source /workspaces/bookAppointmentSystem/deploy/.env

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

# 创建或更新 ECS 服务
create_ecs_services() {
    log "创建/更新 ECS 服务..."
    
    # 获取 CloudFormation 输出值
    CLUSTER_NAME=$(aws cloudformation describe-stacks \
        --stack-name ${STACK_NAME} \
        --query 'Stacks[0].Outputs[?OutputKey==`ECSClusterName`].OutputValue' \
        --output text --region ${AWS_REGION})
    
    FRONTEND_TG_ARN=$(aws cloudformation describe-stacks \
        --stack-name ${STACK_NAME} \
        --query 'Stacks[0].Outputs[?OutputKey==`FrontendTargetGroupArn`].OutputValue' \
        --output text --region ${AWS_REGION})
    
    BACKEND_TG_ARN=$(aws cloudformation describe-stacks \
        --stack-name ${STACK_NAME} \
        --query 'Stacks[0].Outputs[?OutputKey==`BackendTargetGroupArn`].OutputValue' \
        --output text --region ${AWS_REGION})
    
    ECS_SECURITY_GROUP=$(aws cloudformation describe-stacks \
        --stack-name ${STACK_NAME} \
        --query 'Stacks[0].Outputs[?OutputKey==`ECSSecurityGroupId`].OutputValue' \
        --output text --region ${AWS_REGION})
    
    # 获取 VPC 子网
    SUBNETS=$(aws ec2 describe-subnets \
        --filters "Name=tag:Name,Values=*private*" \
        --query 'Subnets[].SubnetId' \
        --output text --region ${AWS_REGION})
    
    SUBNET_LIST=$(echo $SUBNETS | tr ' ' ',')
    
    log "集群名称: ${CLUSTER_NAME}"
    log "前端目标组: ${FRONTEND_TG_ARN}"
    log "后端目标组: ${BACKEND_TG_ARN}"
    log "安全组: ${ECS_SECURITY_GROUP}"
    log "子网: ${SUBNET_LIST}"
    
    # 创建后端服务
    log "创建后端服务..."
    if aws ecs describe-services --cluster ${CLUSTER_NAME} --services ${BACKEND_SERVICE_NAME} --region ${AWS_REGION} &>/dev/null; then
        log "更新现有后端服务..."
        aws ecs update-service \
            --cluster ${CLUSTER_NAME} \
            --service ${BACKEND_SERVICE_NAME} \
            --task-definition pet-care-backend \
            --force-new-deployment \
            --region ${AWS_REGION}
    else
        log "创建新的后端服务..."
        aws ecs create-service \
            --cluster ${CLUSTER_NAME} \
            --service-name ${BACKEND_SERVICE_NAME} \
            --task-definition pet-care-backend \
            --desired-count 2 \
            --launch-type FARGATE \
            --platform-version LATEST \
            --network-configuration "awsvpcConfiguration={subnets=[${SUBNET_LIST}],securityGroups=[${ECS_SECURITY_GROUP}],assignPublicIp=DISABLED}" \
            --load-balancers "targetGroupArn=${BACKEND_TG_ARN},containerName=backend,containerPort=9193" \
            --health-check-grace-period-seconds 300 \
            --region ${AWS_REGION}
    fi
    
    # 创建前端服务
    log "创建前端服务..."
    if aws ecs describe-services --cluster ${CLUSTER_NAME} --services ${FRONTEND_SERVICE_NAME} --region ${AWS_REGION} &>/dev/null; then
        log "更新现有前端服务..."
        aws ecs update-service \
            --cluster ${CLUSTER_NAME} \
            --service ${FRONTEND_SERVICE_NAME} \
            --task-definition pet-care-frontend \
            --force-new-deployment \
            --region ${AWS_REGION}
    else
        log "创建新的前端服务..."
        aws ecs create-service \
            --cluster ${CLUSTER_NAME} \
            --service-name ${FRONTEND_SERVICE_NAME} \
            --task-definition pet-care-frontend \
            --desired-count 2 \
            --launch-type FARGATE \
            --platform-version LATEST \
            --network-configuration "awsvpcConfiguration={subnets=[${SUBNET_LIST}],securityGroups=[${ECS_SECURITY_GROUP}],assignPublicIp=DISABLED}" \
            --load-balancers "targetGroupArn=${FRONTEND_TG_ARN},containerName=frontend,containerPort=80" \
            --health-check-grace-period-seconds 300 \
            --region ${AWS_REGION}
    fi
    
    log "ECS 服务创建/更新完成 ✓"
}

# 等待服务稳定
wait_for_services() {
    log "等待服务稳定..."
    
    aws ecs wait services-stable \
        --cluster ${CLUSTER_NAME} \
        --services ${BACKEND_SERVICE_NAME} ${FRONTEND_SERVICE_NAME} \
        --region ${AWS_REGION}
    
    log "服务已稳定运行 ✓"
}

# 显示部署信息
show_deployment_info() {
    log "部署信息:"
    
    ALB_DNS=$(aws cloudformation describe-stacks \
        --stack-name ${STACK_NAME} \
        --query 'Stacks[0].Outputs[?OutputKey==`ALBDNSName`].OutputValue' \
        --output text --region ${AWS_REGION})
    
    echo "----------------------------------------"
    echo "🎉 部署完成!"
    echo "----------------------------------------"
    echo "前端访问地址: http://${ALB_DNS}"
    echo "后端 API 地址: http://${ALB_DNS}/api/v1"
    echo "ECS 集群: ${CLUSTER_NAME}"
    echo "AWS 区域: ${AWS_REGION}"
    echo "----------------------------------------"
    echo "后续步骤:"
    echo "1. 配置域名 CNAME 记录指向: ${ALB_DNS}"
    echo "2. 在 ALB 配置 SSL 证书"
    echo "3. 更新前端环境变量中的 API URL"
    echo "4. 配置 CloudWatch 监控和告警"
    echo "----------------------------------------"
}

# 主函数
main() {
    log "开始创建 ECS 服务..."
    
    create_ecs_services
    wait_for_services
    show_deployment_info
    
    log "ECS 服务部署完成! 🚀"
}

main "$@"