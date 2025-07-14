# AdoptOpenJDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 결과물(JAR 파일)을 컨테이너 내부로 복사
# build/libs 디렉토리에 생성되는 JAR 파일 이름을 확인해야 합니다.
# 예: insurance-project-0.0.1-SNAPSHOT.jar
COPY build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]