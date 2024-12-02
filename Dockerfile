# Dockerfile
FROM bellsoft/liberica-openjdk-alpine:17

# 시스템 시간대를 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

# 필수 패키지 설치
RUN apk add --no-cache \
    curl \
    wget \
    unzip \
    nss \
    bash \
    libstdc++ \
    libx11 \
    libxcomposite \
    libxdamage \
    libxext \
    libxrandr \
    libxi \
    libatk \
    gtk+3.0 \
    libxcb \
    libnss3 \
    libglib \
    libgobject \
    mesa-gl \

# Google Chrome 다운로드 및 설치
RUN wget https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.85/linux64/chrome-linux64.zip \
    && unzip chrome-linux64.zip \
    && mv chrome-linux64 /usr/local/google-chrome \
    && ln -s /usr/local/google-chrome/chrome /usr/bin/google-chrome

# ChromeDriver 다운로드 및 설치
RUN wget https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.85/linux64/chromedriver-linux64.zip \
    && unzip chromedriver-linux64.zip \
    && mv chromedriver-linux64/chromedriver /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromedriver

# JAR 파일 복사
ARG JAR_FILE=build/libs/sw-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# JVM 시간대 설정을 포함한 애플리케이션 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]