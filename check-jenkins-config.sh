#!/bin/bash
# Jenkins ECS Agent配置检查脚本

echo "🔍 Jenkins ECS Agent配置检查工具"
echo "=================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查AWS CLI
echo -e "\n📋 检查AWS CLI配置..."
if command -v aws &> /dev/null; then
    echo -e "${GREEN}✅ AWS CLI已安装${NC}"
    aws --version
    
    # 检查AWS凭证
    if aws sts get-caller-identity &> /dev/null; then
        echo -e "${GREEN}✅ AWS凭证配置正确${NC}"
        aws sts get-caller-identity
    else
        echo -e "${RED}❌ AWS凭证配置有问题${NC}"
    fi
else
    echo -e "${RED}❌ AWS CLI未安装${NC}"
fi

# 检查ECS集群
echo -e "\n🏗️ 检查ECS集群..."
ECS_CLUSTER="pet-care-cluster"
if aws ecs describe-clusters --clusters $ECS_CLUSTER --region us-east-1 &> /dev/null; then
    echo -e "${GREEN}✅ ECS集群 $ECS_CLUSTER 存在${NC}"
    aws ecs describe-clusters --clusters $ECS_CLUSTER --region us-east-1 --query 'clusters[0].status'
else
    echo -e "${RED}❌ ECS集群 $ECS_CLUSTER 不存在或无法访问${NC}"
fi

# 检查ECR仓库
echo -e "\n📦 检查ECR仓库..."
REPOS=("pet-care-backend" "pet-care-frontend")
for repo in "${REPOS[@]}"; do
    if aws ecr describe-repositories --repository-names $repo --region us-east-1 &> /dev/null; then
        echo -e "${GREEN}✅ ECR仓库 $repo 存在${NC}"
    else
        echo -e "${RED}❌ ECR仓库 $repo 不存在${NC}"
    fi
done

# 检查ALB
echo -e "\n🔄 检查ALB配置..."
ALB_DNS="pet-care-alb-324602613.eu-west-1.elb.amazonaws.com"
if curl -s --connect-timeout 10 http://$ALB_DNS &> /dev/null; then
    echo -e "${GREEN}✅ ALB $ALB_DNS 可访问${NC}"
else
    echo -e "${YELLOW}⚠️ ALB $ALB_DNS 当前不可访问（可能服务未启动）${NC}"
fi

# 检查Jenkins配置建议
echo -e "\n⚙️ Jenkins配置建议检查..."
echo "请在Jenkins中确认以下配置："
echo "1. ECS Agent Template Label: ecs-agent"
echo "2. AWS凭证ID: aws-credentials"  
echo "3. Maven工具名称: Maven-3.9.0"
echo "4. NodeJS工具名称: NodeJS-18"

# 检查Docker
echo -e "\n🐳 检查Docker配置..."
if command -v docker &> /dev/null; then
    echo -e "${GREEN}✅ Docker已安装${NC}"
    docker --version
    
    # 测试ECR登录
    if aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 614441038924.dkr.ecr.us-east-1.amazonaws.com 2>/dev/null; then
        echo -e "${GREEN}✅ ECR登录测试成功${NC}"
    else
        echo -e "${RED}❌ ECR登录测试失败${NC}"
    fi
else
    echo -e "${RED}❌ Docker未安装${NC}"
fi

echo -e "\n🎯 检查完成！"
echo "如果发现问题，请参考jenkins-ecs-agent-setup.md文档进行修复。"