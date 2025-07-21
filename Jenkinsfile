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
                sh 'echo "JAVA_HOME is: $JAVA_HOME"'
                sh '$JAVA_HOME/bin/java -version'
                sh 'chmod +x gradlew'
                sh './gradlew clean build -Dorg.gradle.java.home=$JAVA_HOME'
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
                        sh 'cat Dockerfile'
                sh "docker build --no-cache -t skyrius6732/insurance-project:latest ."
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
                    echo '--- Stage: Deploy (Zero-Downtime) ---'

                    // 기존에 수동으로 실행되었을 수 있는 insurance-project 컨테이너를 정리합니다.
                    // 이 컨테이너는 무중단 배포 로직에 의해 관리되지 않으므로,
                    // 포트 충돌을 방지하기 위해 먼저 정리합니다.
                    sh 'docker stop insurance-project || true'
                    sh 'docker rm insurance-project || true'

                    // 현재 활성 포트를 저장할 파일 경로
                    def currentActivePortFile = "${WORKSPACE}/current_active_port.txt"
                    // 첫 배포 시 기본 활성 포트
                    def currentActivePort = '8081'

                    // 이전에 배포된 활성 포트가 있는지 확인
                    if (fileExists(currentActivePortFile)) {
                        currentActivePort = readFile(currentActivePortFile).trim()
                        echo "이전 활성 포트: ${currentActivePort}"
                    } else {
                        echo "이전 활성 포트가 없습니다. 초기 배포를 위해 ${currentActivePort}를 기본값으로 사용합니다."
                    }

                    def newDeploymentPort // 새로 배포될 애플리케이션의 포트 (그린)
                    def oldDeploymentPort // 현재 트래픽을 받고 있는 애플리케이션의 포트 (블루)

                    // 포트 교체 로직 (8081 <-> 8082)
                    if (currentActivePort == '8081') {
                        newDeploymentPort = '8082'
                        oldDeploymentPort = '8081'
                    } else {
                        newDeploymentPort = '8081'
                        oldDeploymentPort = '8082'
                    }

                    echo "새로운 버전은 ${newDeploymentPort} 포트에 배포됩니다."
                    echo "이전 버전은 (있다면) ${oldDeploymentPort} 포트에서 실행 중입니다."

                    def newContainerName = "insurance-project-${newDeploymentPort}"
                    def oldContainerName = "insurance-project-${oldDeploymentPort}"

                    // 1. 새로 배포될 컨테이너 이름으로 이미 존재하는 컨테이너가 있다면 중지하고 삭제합니다.
                    // (이전 배포가 실패하여 잔여 컨테이너가 남아있을 경우를 대비)
                    echo "잠재적으로 남아있는 새 컨테이너 (${newContainerName})를 중지하고 삭제합니다..."
                    // 컨테이너가 존재하는지 확인하고, 존재하면 중지 및 삭제
                    def containerExists = sh(script: "docker ps -a --filter name=${newContainerName} --format '{{.ID}}'", returnStdout: true).trim()
                    if (containerExists) {
                        sh "docker stop ${newContainerName}"
                        sh "docker rm ${newContainerName}"
                    } else {
                        echo "컨테이너 ${newContainerName}는 존재하지 않습니다. 건너뜁니다."
                    }

                    // 2. 새로운 버전의 컨테이너를 실행합니다.
                    echo "새로운 컨테이너 (${newContainerName})를 ${newDeploymentPort} 포트에 실행합니다..."
                    sh "docker run -d --name ${newContainerName} --network insurance-project_insurance-network -p ${newDeploymentPort}:8080 -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 skyrius6732/insurance-project:latest"

                    // --- 진단용 명령어 추가 임시 ---
                    echo "Checking status of ${newContainerName}..."
                    sh "docker ps -a --filter \"name=${newContainerName}\""
                    echo "Fetching logs for ${newContainerName}..."
                    def appLogs = sh(script: "docker logs ${newContainerName}", returnStdout: true).trim()
                    echo "--- Application Logs Start ---"
                    echo "${appLogs}"
                    echo "--- Application Logs End ---"
                    // --- 진단용 명령어 끝 ---

                    // 3. 새로운 컨테이너의 헬스 체크를 수행합니다.
                    echo "새로운 컨테이너 (${newContainerName})의 헬스 체크를 수행합니다..."
                    // Spring Boot 애플리케이션의 헬스 체크 URL (기본 경로 사용)
                    def healthCheckUrl = "http://${newContainerName}:8080"
                    def maxAttempts = 5 // 최대 시도 횟수
                    def attempt = 0
                    def healthy = false

                    while (attempt < maxAttempts) {
                        try {
                            // curl을 사용하여 HTTP 상태 코드만 가져옵니다.
                            def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' ${healthCheckUrl}", returnStdout: true).trim()
                            if (response == '200') {
                                echo "헬스 체크 통과: ${newContainerName}."
                                healthy = true
                                break
                            } else {
                                echo "헬스 체크 실패: ${newContainerName} (HTTP ${response}). 5초 후 재시도..."
                            }
                        } catch (e) {
                            echo "헬스 체크 실패: ${newContainerName} (오류: ${e.message}). 5초 후 재시도..."
                        }
                        sleep 5 // 5초 대기
                        attempt++
                    }

                    if (!healthy) {
                        error "새로운 컨테이너 (${newContainerName})가 ${maxAttempts}번 시도 후 헬스 체크에 실패했습니다. 배포를 중단합니다."
                    }

                    // 4. Nginx 설정을 업데이트하여 새로운 컨테이너로 트래픽을 전환합니다.
                    echo "Nginx 설정을 ${newContainerName} (${newDeploymentPort} 포트)로 업데이트합니다..."
                    def nginxConfContent = """
        		server {
            			listen 80;
            			server_name localhost;

            			 location / {
                           proxy_pass http://${newContainerName}:8080; # Nginx도 컨테이너 내부의 8080 포트로 전달
                           proxy_set_header Host \\\$host; # \$host -> \\\$host
                           proxy_set_header X-Real-IP \\\$remote_addr; # \$remote_addr -> \\\$remote_addr
                           proxy_set_header X-Forwarded-For \\\$proxy_add_x_forwarded_for; # \$proxy_add_x_forwarded_for -> \\\$proxy_add_x_forwarded_for
                           proxy_set_header X-Forwarded-Proto \\\$scheme; # \$scheme -> \\\$scheme
                         }
        		}
        		"""
                    // 호스트의 Nginx 설정 파일에 내용을 덮어씁니다. (홈디렉토리 권한문제로 사용 x 호스트 -> 컨테이너)
                    //writeFile(file: "/home/skyrius/nginx/conf.d/insurance-project.conf", text: nginxConfContent)

                     // 이렇게 하면 Nginx 컨테이너의 마운트된 볼륨을 통해 호스트에도 반영됩니다.(컨테이너 -> 호스트)
                    sh "echo \"\"\"${nginxConfContent}\"\"\" | docker exec -i nginx-proxy tee /etc/nginx/conf.d/insurance-project.conf"

                    // 5. Nginx 설정을 재로드하여 변경 사항을 적용합니다.
                    echo "Nginx 설정을 재로드합니다..."
                    sh "docker exec nginx-proxy nginx -s reload"

                    // 6. 이전 버전의 컨테이너를 중지하고 삭제합니다.
                    echo "이전 컨테이너 (${oldContainerName})를 중지하고 삭제합니다..."
                    sh "docker stop ${oldContainerName} || true"
                    sh "docker rm ${oldContainerName} || true"

                    // 7. 현재 활성 포트 정보를 파일에 업데이트합니다.
                    echo "현재 활성 포트를 ${newDeploymentPort}로 업데이트합니다..."
                    writeFile(file: currentActivePortFile, text: newDeploymentPort)

                    echo '무중단 배포가 완료되었습니다.'
                }
            }
        }

    }
}