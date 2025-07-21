# --- 빌더 스테이지 ---
# 애플리케이션을 빌드하는 데 사용되는 스테이지
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Gradle 빌드에 필요한 파일들을 먼저 복사하여 캐싱 활용
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .
COPY src/main src/main

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# Gradle 빌드 실행
RUN ./gradlew bootJar --no-daemon -x test

# JAR 파일 생성 확인 (디버깅용)
RUN ls -l build/libs

# --- 런타임 스테이지 ---
# 최종 애플리케이션 이미지를 생성하는 스테이지
FROM eclipse-temurin:17-jre-jammy
#FROM eclipse-temurin:17-jre-bullseye

WORKDIR /app

# netcat, dnsutils, iputils-ping 설치 (Kafka 연결 대기 스크립트 및 진단용)
#RUN apt-get update && \
#    apt-get install -y netcat-traditional dnsutils iputils-ping && \
#    rm -rf /var/lib/apt/lists/*

# 우분투 패키지 서버
#RUN rm -rf /var/lib/apt/lists/* && \
#    apt-get update && \
#    apt-get install -y netcat-traditional dnsutils iputils-ping && \
#    apt-get clean && \
#    rm -rf /var/lib/apt/lists/*

# 카카오 미러 서버
RUN sed -i 's/archive.ubuntu.com/mirror.kakao.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y netcat-traditional dnsutils iputils-ping & \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# 빌더 스테이지에서 생성된 JAR 파일을 복사
COPY --from=builder /app/build/libs/insurance-project-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션이 사용할 포트 노출
EXPOSE 8080

# 애플리케이션 실행 명령 정의
 ENTRYPOINT ["java", "-jar", "app.jar"]
#ENTRYPOINT ["/bin/bash", "-c", "echo \"--- Debugging Kafka Bootstrap Servers ---\"; echo \"SPRING_KAFKA_BOOTSTRAP_SERVERS is: ${SPRING_KAFKA_BOOTSTRAP_SERVERS}\"; echo \"--- Starting Application ---\"; java -jar app.jar"]