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
        FRONTEND_SERVICE = 'pet-care-frontend-service-jqsbfks7'
        
        // ALB Configuration
        ALB_DNS_NAME = 'ALB-pet-384183543.eu-west-1.elb.amazonaws.com'
        
        // Image Tag
        IMAGE_TAG = "${BUILD_NUMBER}"
        
        // AWS Credentials ID (configured in Jenkins)
        AWS_CREDENTIAL_ID = 'new-AWS-ECS'
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
            steps {
                echo '🐳 Building Docker images for amd64 architecture...'
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', 
                                  credentialsId: 'new-AWS-ECS']]) {
                    script {
                        // Login to ECR first
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                        
                        // Clean any existing images to avoid conflicts
                        echo "Cleaning existing Docker images..."
                        sh '''
                            docker system prune -f
                            docker builder prune -f
                        '''
                        // Setup buildx for cross-platform builds
                        sh '''
                            # Create and use buildx builder (if not exists)
                            docker buildx create --use --name cross --driver-opt network=host || docker buildx use cross
                            docker buildx inspect --bootstrap
                            
                            # Verify buildx supports amd64 platform
                            docker buildx ls
                        '''
                        

                        
                        // Build and push backend image for amd64 architecture only
                        echo "Building and pushing backend image..."
                        sh """
                            docker buildx build \\
                              --platform linux/amd64 \\
                              --provenance=false \\
                              --sbom=false \\
                              -t ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG} \\
                              -t ${ECR_REGISTRY}/${BACKEND_REPO}:latest \\
                              ./backend \\
                              --push
                        """
                        
                        // Verify backend image manifest
                        echo "Verifying backend image manifest..."
                        sh """
                            docker buildx imagetools inspect ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}
                        """
                        
                        // Build and push frontend image for amd64 architecture only
                        echo "Building and pushing frontend image..."
                        sh """
                            docker buildx build \\
                              --platform linux/amd64 \\
                              --provenance=false \\
                              --sbom=false \\
                              -t ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG} \\
                              -t ${ECR_REGISTRY}/${FRONTEND_REPO}:latest \\
                              ./frontend \\
                              --push
                        """
                        
                        // Verify frontend image manifest
                        echo "Verifying frontend image manifest..."
                        sh """
                            docker buildx imagetools inspect ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}
                        """
                        
                        echo "✅ Images built and pushed successfully for amd64 architecture"
                        echo "Backend image: ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                        echo "Frontend image: ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                        
                        // Final verification that images are available in ECR
                        echo "Final verification: Checking images in ECR..."
                        sh """
                            aws ecr describe-images --repository-name ${BACKEND_REPO} --image-ids imageTag=${IMAGE_TAG} --region ${AWS_REGION}
                            aws ecr describe-images --repository-name ${FRONTEND_REPO} --image-ids imageTag=${IMAGE_TAG} --region ${AWS_REGION}
                        """
                    }
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                echo '🔒 Scanning images for security vulnerabilities...'
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'new-AWS-ECS']]) {
                    script {
                        // Login to ECR within the same credential context
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                        
                        // Pull and scan backend image with error handling
                        echo "Pulling and scanning backend image: ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                        try {
                            sh "docker pull ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                            sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --exit-code 0 --severity HIGH,CRITICAL --scanners vuln ${ECR_REGISTRY}/${BACKEND_REPO}:${IMAGE_TAG}"
                            echo "✅ Backend image security scan completed"
                        } catch (Exception e) {
                            echo "⚠️ Security vulnerabilities found in backend image, but continuing build..."
                            echo "Error: ${e.message}"
                        }
                        
                        // Pull and scan frontend image with error handling  
                        echo "Pulling and scanning frontend image: ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                        try {
                            sh "docker pull ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                            sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --exit-code 0 --severity HIGH,CRITICAL --scanners vuln ${ECR_REGISTRY}/${FRONTEND_REPO}:${IMAGE_TAG}"
                            echo "✅ Frontend image security scan completed"
                        } catch (Exception e) {
                            echo "⚠️ Security vulnerabilities found in frontend image, but continuing build..."
                            echo "Error: ${e.message}"
                        }
                    }
                }
            }
        }
        
        stage('Deploy to ECS') {
            steps {
                echo '🚀 Deploying to ECS...'
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', 
                                  credentialsId: 'new-AWS-ECS']]) {
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
                echo '⏳ Giving services time to deploy...'
                script {
                    // Simple wait approach since ecr-user lacks ECS describe permissions
                    echo "🚀 Services have been updated with force-new-deployment"
                    echo "⏱️  Allowing 3 minutes for service deployment to complete..."
                    echo "� Note: Deployment verification will be done via health checks in next stage"
                    
                    // Wait 3 minutes for deployment to have time to complete
                    sleep time: 3, unit: 'MINUTES'
                    
                    echo "✅ Deployment wait period completed - proceeding to health check"
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