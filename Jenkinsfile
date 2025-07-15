pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo '--- Stage: Checkout ---'
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
                     echo '--- Stage: Build Docker Image ---'
                     echo 'Building Docker image for insurance-project...'
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
                    echo '--- Stage: Push Docker Image ---'
                    echo 'Pushing Docker image to Docker Hub...'
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
                       echo '--- Stage: Deploy ---'
                       echo 'Stopping and removing old insurance-project container...'
                       // 기존 insurance-project 컨테이너가 실행 중이라면 중지하고 삭제합니다.
                       // '|| true'는 컨테이너가 존재하지 않거나 실행 중이 아니어도 스크립트가 실패하지 않도록 합니다.
                       sh 'docker stop insurance-project || true'
                       sh 'docker rm insurance-project || true'

                       // 새로운 insurance-project 컨테이너를 실행합니다.
                       // -d: 백그라운드에서 실행
                       // --name: 컨테이너 이름 지정
                       // --network: 'insurance-net' 네트워크에 연결
                       // -p 8080:8080: 호스트의 8080 포트를 컨테이너의 8080 포트에 매핑
                       sh 'docker run -d --name insurance-project --network insurance-net -p 8081:8080 skyrius6732/insurance-project:latest'
                   }
               }
        }
    }
}