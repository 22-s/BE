# Dockerfile
FROM bellsoft/liberica-openjdk-alpine:17

# 시스템 시간대를 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

# Chrome 및 ChromeDriver 설치
RUN apk add --no-cache \
    curl \
    wget \
    unzip \
    nss \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    rpm -i google-chrome-stable_current_x86_64.rpm && \
    rm -f google-chrome-stable_current_x86_64.rpm

# JAR 파일 복사
ARG JAR_FILE=build/libs/sw-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# JVM 시간대 설정을 포함한 애플리케이션 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]