pipeline {
    agent any

    environment {
        REMOTE_HOST = '34.244.208.35'
        REMOTE_USER = 'ubuntu'
        REMOTE_DIR  = '/home/ubuntu/bookAppointmentSystem'
        GIT_REPO    = 'https://github.com/2804622326/bookAppointmentSystem.git'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the source code from the same branch
                checkout scm
            }
        }

        stage('Test Backend') {
            steps {
                dir('backend') {
                    sh 'mvn -B test'
                }
            }
        }

        stage('Test Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install && npm run test'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t book-backend ./backend'
                sh 'docker build -t book-frontend ./frontend'
            }
        }

        stage('Deploy to AWS') {
            steps {
                // Use Jenkins SSH credentials to connect to the remote host
                sshagent(['aws-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} '
                            if [ ! -d ${REMOTE_DIR} ]; then
                                git clone ${GIT_REPO} ${REMOTE_DIR}
                            else
                                cd ${REMOTE_DIR} && git pull
                            fi
                            cd ${REMOTE_DIR} && docker compose down && docker compose up --build -d
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment succeeded.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
