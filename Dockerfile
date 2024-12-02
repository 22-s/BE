# Base Image
FROM bellsoft/liberica-openjdk-alpine:17

# 시스템 시간대 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

# Edge(최신) 리포지토리 활성화 및 패키지 설치
RUN echo "https://dl-cdn.alpinelinux.org/alpine/edge/main" >> /etc/apk/repositories \
    && echo "https://dl-cdn.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories \
    && apk update \
    && apk add --no-cache \
        curl \
        wget \
        unzip \
        bash \
        libstdc++ \
        libx11 \
        libxcomposite \
        libxdamage \
        libxext \
        libxrandr \
        libxi \
        libxcb \
        libnss3 \
        libglib \
        libgobject \
        mesa-gl \
        dbus

# Google Chrome 설치
RUN wget -O /tmp/chrome-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.85/linux64/chrome-linux64.zip \
    && unzip /tmp/chrome-linux64.zip -d /usr/local/ \
    && mv /usr/local/chrome-linux64 /usr/local/google-chrome \
    && ln -s /usr/local/google-chrome/chrome /usr/bin/google-chrome \
    && rm -rf /tmp/chrome-linux64.zip

# ChromeDriver 설치
RUN wget -O /tmp/chromedriver-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.85/linux64/chromedriver-linux64.zip \
    && unzip /tmp/chromedriver-linux64.zip -d /usr/local/ \
    && mv /usr/local/chromedriver-linux64/chromedriver /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromedriver \
    && rm -rf /tmp/chromedriver-linux64.zip /usr/local/chromedriver-linux64

# JAR 파일 복사
ARG JAR_FILE=build/libs/sw-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# JVM 시간대 설정 및 애플리케이션 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
