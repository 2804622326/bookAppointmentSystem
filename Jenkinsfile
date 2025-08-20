pipeline {
  agent any

  environment {
    AWS_REGION     = 'eu-west-1'
    AWS_ACCOUNT_ID = '614441038924'
    ECR_URI        = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

    ECR_BACKEND_REPO  = 'upc-backend'
    ECR_FRONTEND_REPO = 'upc-frontend'
    ECR_BACKEND       = "${ECR_URI}/${ECR_BACKEND_REPO}"
    ECR_FRONTEND      = "${ECR_URI}/${ECR_FRONTEND_REPO}"

    ECS_CLUSTER = 'book-appointment-vet'
    ECS_SERVICE = 'bookApp-3-service-35iquf20'
    TASK_FAMILY = 'bookApp-3'

    BACKEND_CONTAINER_NAME  = 'backend'
    FRONTEND_CONTAINER_NAME = 'frontend'

    IMAGE_TAG = "${env.GIT_COMMIT?.take(7) ?: env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Test Backend') {
      steps {
        dir('backend') {
          sh 'mvn -B -DskipTests=false test'
        }
      }
    }

    stage('Test Frontend') {
      steps {
        dir('frontend') {
          sh '''
            npm ci || npm install
            npm test -- --ci --watchAll=false || true
          '''
        }
      }
    }

    stage('Build Docker Images') {
      steps {
        sh '''
          docker build -t upc-backend:build  ./backend
          docker build -t upc-frontend:build ./frontend
        '''
      }
    }

    stage('Login to ECR & Push Images') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          sh '''
            set -e
            export AWS_DEFAULT_REGION=${AWS_REGION}

            aws --version
            jq --version >/dev/null 2>&1 || (echo "jq not found"; exit 1)
            docker --version

            aws ecr describe-repositories --repository-names ${ECR_BACKEND_REPO} >/dev/null 2>&1 || \
              aws ecr create-repository --repository-name ${ECR_BACKEND_REPO}
            aws ecr describe-repositories --repository-names ${ECR_FRONTEND_REPO} >/dev/null 2>&1 || \
              aws ecr create-repository --repository-name ${ECR_FRONTEND_REPO}

            aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | \
              docker login --username AWS --password-stdin ${ECR_URI}

            docker tag upc-backend:build  ${ECR_BACKEND}:${IMAGE_TAG}
            docker tag upc-frontend:build ${ECR_FRONTEND}:${IMAGE_TAG}

            docker push ${ECR_BACKEND}:${IMAGE_TAG}
            docker push ${ECR_FRONTEND}:${IMAGE_TAG}
          '''
        }
      }
    }

    stage('Update Task Definition & Deploy to ECS') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
          timeout(time: 20, unit: 'MINUTES') {
            sh '''
              set -e
              export AWS_DEFAULT_REGION=${AWS_REGION}

              aws ecs describe-task-definition --task-definition ${TASK_FAMILY} \
                --query 'taskDefinition' > current-td.json

              NEW_BACKEND_IMAGE="${ECR_BACKEND}:${IMAGE_TAG}"
              NEW_FRONTEND_IMAGE="${ECR_FRONTEND}:${IMAGE_TAG}"

              cat current-td.json \
                | jq 'del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .compatibilities, .registeredAt, .registeredBy)' \
                | jq --arg bn "${BACKEND_CONTAINER_NAME}" --arg bi "${NEW_BACKEND_IMAGE}" \
                     --arg fn "${FRONTEND_CONTAINER_NAME}" --arg fi "${NEW_FRONTEND_IMAGE}" \
                     ' .containerDefinitions = (.containerDefinitions
                         | map( if .name == $bn then .image = $bi | . else . end )
                         | map( if .name == $fn then .image = $fi | . else . end )) ' \
                > new-td.json

              NEW_TD_ARN=$(aws ecs register-task-definition \
                --cli-input-json file://new-td.json \
                --query 'taskDefinition.taskDefinitionArn' --output text)

              aws ecs update-service --cluster "${ECS_CLUSTER}" --service "${ECS_SERVICE}" \
                --task-definition "$NEW_TD_ARN" --force-new-deployment >/dev/null

              aws ecs wait services-stable --cluster "${ECS_CLUSTER}" --services "${ECS_SERVICE}"
            '''
          }
        }
      }
    }
  }

  post {
    success { echo 'Deployment succeeded.' }
    failure { echo 'Pipeline failed.' }
  }
}