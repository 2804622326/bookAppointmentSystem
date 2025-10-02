#!/bin/bash

# Pet Care System - AWS ECS 一键部署脚本
# 使用前请确保已配置 AWS CLI: aws configure

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

# 输出函数
print_banner() {
    echo -e "${CYAN}"
    echo "======================================"
    echo "   Pet Care System - AWS ECS 部署    "
    echo "======================================"
    echo -e "${NC}"
}

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

step() {
    echo -e "${BLUE}[STEP] $1${NC}"
}

# 检查依赖
check_prerequisites() {
    step "检查前提条件..."
    
    # 检查 AWS CLI
    if ! command -v aws &> /dev/null; then
        error "AWS CLI 未安装，请先安装 AWS CLI"
    fi
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker"
    fi
    
    # 检查 AWS 凭证
    if ! aws sts get-caller-identity &> /dev/null; then
        error "AWS 凭证未配置，请运行: aws configure"
    fi
    
    # 检查当前目录
    if [[ ! -f "docker-compose.yml" ]]; then
        error "请在项目根目录运行此脚本"
    fi
    
    log "前提条件检查完成 ✓"
}

# 用户输入配置
gather_user_input() {
    step "收集部署配置..."
    
    echo -e "${PURPLE}请输入以下配置信息:${NC}"
    echo
    
    # VPC ID
    read -p "请输入 VPC ID (vpc-xxxxxxxxx): " VPC_ID
    if [[ ! $VPC_ID =~ ^vpc- ]]; then
        error "无效的 VPC ID 格式"
    fi
    
    # 子网 IDs
    read -p "请输入公有子网 1 ID (subnet-xxxxxxxxx): " PUBLIC_SUBNET_1
    read -p "请输入公有子网 2 ID (subnet-xxxxxxxxx): " PUBLIC_SUBNET_2
    read -p "请输入私有子网 1 ID (subnet-xxxxxxxxx): " PRIVATE_SUBNET_1
    read -p "请输入私有子网 2 ID (subnet-xxxxxxxxx): " PRIVATE_SUBNET_2
    
    # 数据库密码
    while true; do
        read -s -p "请输入数据库密码 (至少8位字符): " DB_PASSWORD
        echo
        if [[ ${#DB_PASSWORD} -ge 8 ]]; then
            break
        else
            warn "密码长度至少需要8位字符"
        fi
    done
    
    # 域名
    read -p "请输入域名 (例: example.com): " DOMAIN_NAME
    if [[ -z "$DOMAIN_NAME" ]]; then
        DOMAIN_NAME="your-domain.com"
    fi
    
    echo
    log "配置收集完成"
    echo "VPC ID: $VPC_ID"
    echo "公有子网: $PUBLIC_SUBNET_1, $PUBLIC_SUBNET_2"
    echo "私有子网: $PRIVATE_SUBNET_1, $PRIVATE_SUBNET_2"
    echo "域名: $DOMAIN_NAME"
    echo
}

# 确认部署
confirm_deployment() {
    echo -e "${YELLOW}即将开始部署，这可能需要 10-15 分钟...${NC}"
    read -p "确认继续? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "部署已取消"
        exit 0
    fi
}

# 创建基础设施
deploy_infrastructure() {
    step "部署基础设施..."
    
    log "创建 CloudFormation 堆栈..."
    aws cloudformation create-stack \
        --stack-name pet-care-infrastructure \
        --template-body file://deploy/infrastructure.yaml \
        --parameters \
            ParameterKey=VpcId,ParameterValue=$VPC_ID \
            ParameterKey=PublicSubnet1,ParameterValue=$PUBLIC_SUBNET_1 \
            ParameterKey=PublicSubnet2,ParameterValue=$PUBLIC_SUBNET_2 \
            ParameterKey=PrivateSubnet1,ParameterValue=$PRIVATE_SUBNET_1 \
            ParameterKey=PrivateSubnet2,ParameterValue=$PRIVATE_SUBNET_2 \
            ParameterKey=DBPassword,ParameterValue="$DB_PASSWORD" \
            ParameterKey=DomainName,ParameterValue=$DOMAIN_NAME \
        --capabilities CAPABILITY_NAMED_IAM \
        --region eu-west-1 || {
        
        warn "堆栈可能已存在，尝试更新..."
        aws cloudformation update-stack \
            --stack-name pet-care-infrastructure \
            --template-body file://deploy/infrastructure.yaml \
            --parameters \
                ParameterKey=VpcId,ParameterValue=$VPC_ID \
                ParameterKey=PublicSubnet1,ParameterValue=$PUBLIC_SUBNET_1 \
                ParameterKey=PublicSubnet2,ParameterValue=$PUBLIC_SUBNET_2 \
                ParameterKey=PrivateSubnet1,ParameterValue=$PRIVATE_SUBNET_1 \
                ParameterKey=PrivateSubnet2,ParameterValue=$PRIVATE_SUBNET_2 \
                ParameterKey=DBPassword,ParameterValue="$DB_PASSWORD" \
                ParameterKey=DomainName,ParameterValue=$DOMAIN_NAME \
            --capabilities CAPABILITY_NAMED_IAM \
            --region eu-west-1 || warn "堆栈更新失败或无变化"
    }
    
    log "等待基础设施创建完成..."
    aws cloudformation wait stack-create-complete \
        --stack-name pet-care-infrastructure \
        --region eu-west-1 || \
    aws cloudformation wait stack-update-complete \
        --stack-name pet-care-infrastructure \
        --region eu-west-1
    
    log "基础设施部署完成 ✓"
}

# 部署容器应用
deploy_containers() {
    step "部署容器应用..."
    
    # 更新任务定义中的数据库端点
    DB_ENDPOINT=$(aws cloudformation describe-stacks \
        --stack-name pet-care-infrastructure \
        --query 'Stacks[0].Outputs[?OutputKey==`DatabaseEndpoint`].OutputValue' \
        --output text --region eu-west-1)
    
    DB_SECRET_ARN=$(aws cloudformation describe-stacks \
        --stack-name pet-care-infrastructure \
        --query 'Stacks[0].Outputs[?OutputKey==`DBPasswordSecretArn`].OutputValue' \
        --output text --region eu-west-1)
    
    # 更新后端任务定义
    sed -i "s|pet-care-db\.cluster-xxxxx\.eu-west-1\.rds\.amazonaws\.com|$DB_ENDPOINT|g" deploy/backend-task-definition.json
    sed -i "s|arn:aws:secretsmanager:eu-west-1:614441038924:secret:pet-care-db-password-xxxxx|$DB_SECRET_ARN|g" deploy/backend-task-definition.json
    sed -i "s|https://your-domain\.com|https://$DOMAIN_NAME|g" deploy/backend-task-definition.json
    
    # 更新前端任务定义
    sed -i "s|https://api\.your-domain\.com|https://api.$DOMAIN_NAME|g" deploy/frontend-task-definition.json
    
    # 执行部署脚本
    log "构建和推送 Docker 镜像..."
    ./deploy/aws-deploy.sh
    
    log "创建 ECS 服务..."
    ./deploy/create-ecs-services.sh
    
    log "容器应用部署完成 ✓"
}

# 显示部署结果
show_results() {
    step "部署完成!"
    
    ALB_DNS=$(aws cloudformation describe-stacks \
        --stack-name pet-care-infrastructure \
        --query 'Stacks[0].Outputs[?OutputKey==`ALBDNSName`].OutputValue' \
        --output text --region eu-west-1)
    
    echo
    echo -e "${GREEN}🎉 Pet Care System 部署成功!${NC}"
    echo -e "${GREEN}==========================================${NC}"
    echo
    echo -e "${CYAN}访问信息:${NC}"
    echo "前端地址: http://$ALB_DNS"
    echo "API 地址: http://$ALB_DNS/api/v1"
    echo
    echo -e "${CYAN}下一步操作:${NC}"
    echo "1. 配置域名 DNS:"
    echo "   - 创建 CNAME 记录: $DOMAIN_NAME -> $ALB_DNS"
    echo "   - 创建 CNAME 记录: api.$DOMAIN_NAME -> $ALB_DNS"
    echo
    echo "2. 配置 SSL 证书:"
    echo "   - 在 AWS Certificate Manager 申请证书"
    echo "   - 将证书绑定到负载均衡器"
    echo
    echo "3. 监控服务:"
    echo "   - CloudWatch: https://console.aws.amazon.com/cloudwatch/"
    echo "   - ECS 控制台: https://console.aws.amazon.com/ecs/"
    echo
    echo -e "${GREEN}==========================================${NC}"
}

# 主函数
main() {
    print_banner
    
    check_prerequisites
    gather_user_input
    confirm_deployment
    
    deploy_infrastructure
    deploy_containers
    
    show_results
    
    log "全部部署完成! 🚀"
}

# 捕获中断信号
trap 'error "部署被中断"' INT TERM

# 执行主函数
main "$@"