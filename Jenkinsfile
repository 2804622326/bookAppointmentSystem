pipeline {
    agent any
    
    environment {
        // AWS Configuration
        AWS_REGION = 'eu-west-1'
        AWS_ACCOUNT_ID = '614441038924'
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        
        // ECR Repositories
        BACKEND_REPO = 'pet-care-backend'
        FRONTEND_REPO = 'pet-care-frontend'
        
        // ECS Configuration
        ECS_CLUSTER = 'pet-care-cluster'
        BACKEND_SERVICE = 'pet-care-backend-service'
        FRONTEND_SERVICE = 'pet-care-frontend-service'
        
        // ALB Configuration
        ALB_DNS_NAME = 'ALB-pet-384183543.eu-west-1.elb.amazonaws.com'
        
        // Image Tag
        IMAGE_TAG = "${BUILD_NUMBER}"
        
        // AWS Credentials ID (configured in Jenkins)
        AWS_CREDENTIAL_ID = 'aws-credentials'
    }
    
    // Removed tools block: use local Maven Wrapper and Docker container NodeJS
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out source code...'
                checkout scm
                echo "Using current branch: ${env.BRANCH_NAME}"
            }
        }
        
        stage('Build & Test Backend') {
            steps {
                echo '🔨 Building and testing backend...'
                dir('backend') {
                    // Run tests
                    sh './mvnw clean test -Dspring.profiles.active=test'
                    
                    // Build application
                    sh './mvnw clean package -DskipTests -Dspring.profiles.active=prod'
                }
                
                // Publish test reports
                junit 'backend/target/surefire-reports/*.xml'
                
                // Archive build artifacts
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            }
        }
        
        stage('Build & Test Frontend') {
            steps {
                echo '🎨 Building and testing frontend...'
                dir('frontend') {
                    // Install dependencies
                    sh 'npm ci'
                    
                    // Run tests
                    sh 'npm run test -- --coverage --watchAll=false'
                    
                    // Build production version
                    sh 'npm run build'
                }
                
                // Publish test coverage report
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'frontend/coverage/lcov-report',
                    reportFiles: 'index.html',
                    reportName: 'Frontend Coverage Report'
                ])
            }
        }
        
        stage('Build Docker Images') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        echo '🐳 Building backend Docker image...'
                        script {
                            def backendImage = docker.build(
                                "${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}",
                                "./backend"
                            )
                            // Tag the image with 'latest' tag
                            sh """
                                docker tag ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG} \\
                                           ${ECR_REGISTRY}/${BACKEND_REPO}:latest
                            """
                        }
                    }
                }
                stage('Build Frontend Image') {
                    steps {
                        echo '🎯 Building frontend Docker image...'
                        script {
                            def frontendImage = docker.build(
                                "${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}",
                                "./frontend"
                            )
                            // Tag the image with 'latest' tag
                            sh """
                                docker tag ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG} \\
                                           ${ECR_REGISTRY}/${FRONTEND_REPO}:latest
                            """
                        }
                    }
                }
            }
        }
        
        stage('Security Scan') {
            parallel {
                stage('Backend Security Scan') {
                    steps {
                        echo '🔒 Scanning backend image for security vulnerabilities...'
                        script {
                            try {
                                sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --exit-code 0 --severity HIGH,CRITICAL ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                            } catch (Exception e) {
                                echo "⚠️  Security vulnerabilities found in backend image, but continuing build..."
                            }
                        }
                    }
                }
                stage('Frontend Security Scan') {
                    steps {
                        echo '🔒 Scanning frontend image for security vulnerabilities...'
                        script {
                            try {
                                sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --exit-code 0 --severity HIGH,CRITICAL ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                            } catch (Exception e) {
                                echo "⚠️  Security vulnerabilities found in frontend image, but continuing build..."
                            }
                        }
                    }
                }
            }
        }
        
        stage('Push to ECR') {
            steps {
                echo '📤 Pushing images to ECR...'
                withCredentials([aws(credentialsId: "${AWS_CREDENTIAL_ID}", region: "${AWS_REGION}")]) {
                    script {
                        // Login to ECR
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                        
                        // Push images
                        sh "docker push ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                        sh "docker push ${ECR_REGISTRY}/${BACKEND_REPO}:latest"
                        
                        sh "docker push ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                        sh "docker push ${ECR_REGISTRY}/${FRONTEND_REPO}:latest"
                        
                        echo "✅ Image push completed"
                        echo "Backend image: ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                        echo "Frontend image: ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                    }
                }
            }
        }
        
        stage('Deploy to ECS') {
            steps {
                echo '🚀 Deploying to ECS...'
                withCredentials([aws(credentialsId: "${AWS_CREDENTIAL_ID}", region: "${AWS_REGION}")]) {
                    script {
                        // Update backend service
                        echo "Updating backend service: ${BACKEND_SERVICE}"
                        sh """
                            aws ecs update-service \
                                --cluster ${ECS_CLUSTER} \
                                --service ${BACKEND_SERVICE} \
                                --force-new-deployment \
                                --region ${AWS_REGION}
                        """
                        
                        // Update frontend service
                        echo "Updating frontend service: ${FRONTEND_SERVICE}"
                        sh """
                            aws ecs update-service \
                                --cluster ${ECS_CLUSTER} \
                                --service ${FRONTEND_SERVICE} \
                                --force-new-deployment \
                                --region ${AWS_REGION}
                        """
                    }
                }
            }
        }
        
        stage('Wait for Deployment') {
            steps {
                echo '⏳ Waiting for deployment completion...'
                withCredentials([aws(credentialsId: "${AWS_CREDENTIAL_ID}", region: "${AWS_REGION}")]) {
                    script {
                        // Wait for services to stabilize
                        sh """
                            echo "Waiting for backend service to stabilize..."
                            aws ecs wait services-stable \
                                --cluster ${ECS_CLUSTER} \
                                --services ${BACKEND_SERVICE} \
                                --region ${AWS_REGION}
                            
                            echo "Waiting for frontend service to stabilize..."
                            aws ecs wait services-stable \
                                --cluster ${ECS_CLUSTER} \
                                --services ${FRONTEND_SERVICE} \
                                --region ${AWS_REGION}
                        """
                    }
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo '🔍 Performing health check...'
                script {
                    def attempts = 0
                    def maxAttempts = 10
                    def success = false
                    
                    while (attempts < maxAttempts && !success) {
                        attempts++
                        echo "Health check attempt ${attempts}/${maxAttempts}"
                        
                        try {
                            // Check backend health status
                            def backendHealth = sh(
                                script: "curl -f -s http://${ALB_DNS_NAME}/actuator/health || echo 'failed'",
                                returnStdout: true
                            ).trim()
                            
                            // Check frontend accessibility
                            def frontendHealth = sh(
                                script: "curl -f -s -I http://${ALB_DNS_NAME} | head -n 1 | grep '200 OK' || echo 'failed'",
                                returnStdout: true
                            ).trim()
                            
                            if (backendHealth != 'failed' && frontendHealth != 'failed') {
                                echo "✅ Health check passed!"
                                echo "Backend status: ${backendHealth}"
                                echo "Frontend status: ${frontendHealth}"
                                success = true
                            } else {
                                echo "❌ Health check failed, waiting 30 seconds before retry..."
                                sleep 30
                            }
                        } catch (Exception e) {
                            echo "❌ Health check exception: ${e.message}"
                            if (attempts < maxAttempts) {
                                echo "Waiting 30 seconds before retry..."
                                sleep 30
                            }
                        }
                    }
                    
                    if (!success) {
                        error("Health check failed! Deployment may have issues.")
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning workspace...'
            
            // Clean local Docker images
            sh '''
                docker image prune -f
                docker container prune -f
            '''
            
            // Clean workspace (requires Workspace Cleanup plugin)
            script {
                try {
                    cleanWs()
                } catch (Exception e) {
                    echo "⚠️ Workspace cleanup failed (plugin not installed): ${e.message}"
                    // Alternative cleanup
                    sh 'rm -rf ./*'
                }
            }
        }
        
        success {
            echo '🎉 Pipeline execution successful!'
            
            // Success notification (optional Slack/email configuration)
            script {
                def message = """
                🎉 Pet Care System deployment successful!
                
                📋 Build Information:
                • Branch: ${env.BRANCH_NAME}
                • Build Number: ${BUILD_NUMBER}
                • Image Tag: ${IMAGE_TAG}
                
                🔗 Links:
                • Frontend: http://ALB-pet-384183543.eu-west-1.elb.amazonaws.com
                • API: http://ALB-pet-384183543.eu-west-1.elb.amazonaws.com/api/v1
                
                📊 Deployment Details:
                • ECS Cluster: ${ECS_CLUSTER}
                • Backend Service: ${BACKEND_SERVICE}
                • Frontend Service: ${FRONTEND_SERVICE}
                """
                
                echo message
                
                // Slack notification (requires Slack plugin configuration)
                // slackSend channel: '#deployments', 
                //           color: 'good', 
                //           message: message
            }
        }
        
        failure {
            echo '❌ Pipeline execution failed!'
            
            // Failure notification
            script {
                def message = """
                ❌ Pet Care System deployment failed!
                
                📋 Build Information:
                • Branch: ${env.BRANCH_NAME}
                • Build Number: ${BUILD_NUMBER}
                • Failed Stage: ${env.STAGE_NAME}
                
                🔍 View Details: ${env.BUILD_URL}
                """
                
                echo message
                
                // Slack notification
                // slackSend channel: '#deployments', 
                //           color: 'danger', 
                //           message: message
            }
        }
        
        unstable {
            echo '⚠️ Pipeline unstable (tests failed but build succeeded)'
        }
    }
}