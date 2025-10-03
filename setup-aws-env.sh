#!/bin/bash

# 设置AWS环境变量的脚本
# 请替换为你的实际AWS凭证

echo "设置AWS环境变量..."

# 请替换为你的实际AWS凭证
export AWS_ACCESS_KEY_ID="YOUR_ACCESS_KEY_HERE"
export AWS_SECRET_ACCESS_KEY="YOUR_SECRET_KEY_HERE" 
export AWS_DEFAULT_REGION="eu-west-1"

echo "AWS环境变量已设置"
echo "现在可以运行: ./manual-build-amd64.sh"

# 验证凭证
aws sts get-caller-identity