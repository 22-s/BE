# Base Image
FROM bellsoft/liberica-openjdk-alpine:17

# 시스템 시간대 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

# 크롤링 관련 패키지 설치
RUN apk add --no-cache \
    curl \
    wget \
    unzip \
    nss \
    chromium \
    chromium-chromedriver

# JAR 파일 복사
ARG JAR_FILE=build/libs/sw-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# JVM 시간대 설정 및 애플리케이션 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
