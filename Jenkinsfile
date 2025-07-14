pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/skyrius6732/insurance-project.git'
            }
        }
        stage('Build') {
            tools {
                jdk 'JDK17' // 젠킨스 Tools 설정에서 지정한 JDK 이름과 일치해야 합니다.
            }
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Dockerfile이 있는 디렉토리로 이동
                    dir('.') {
                        // Docker 이미지 빌드
                        // skyrius6732/insurance-project:latest 대신 실제 Docker Hub 사용자 이름과 이미지 이름을 사용하세요.
                        sh "docker build -t skyrius6732/insurance-project:latest ."
                    }
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    // Docker Hub에 로그인 (젠킨스 Credentials에 Docker Hub 자격 증명 추가 필요)
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo \"$DOCKER_PASSWORD\" | docker login -u \"$DOCKER_USERNAME\" --password-stdin"
                        // 일단은 로그인 없이 푸시 (공개 저장소라면 가능)
                        sh "docker push skyrius6732/insurance-project:latest"
                        // 선택 사항 : 푸시 후 로그아웃(보안상권장)
                        sh "docker logout"
                    }

                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // 배포 서버(WSL2)에 SSH 접속하여 Docker 컨테이너 실행
                    // 이 부분은 실제 환경에 따라 크게 달라집니다.
                    // 예시:
                    // sshagent(credentials: ['ssh-key-to-wsl2']) {
                    //     sh "ssh user@wsl2-ip-address 'docker stop insurance-app || true && docker rm insurance-app || true && docker pull skyrius6732/insurance-project:latest && docker run -d --name insurance-app -p 8081:8080 skyrius6732/insurance-project:latest'"
                    // }
                    echo 'Deployment step - This needs to be configured based on your actual deployment strategy.'
                    echo 'For now, manually deploy or configure SSH access to your WSL2 Docker environment.'
                }
            }
        }
    }
}