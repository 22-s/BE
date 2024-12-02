# Base Image
FROM bellsoft/liberica-openjdk-alpine:17

# 시스템 시간대 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

# 필수 패키지 설치
RUN apk add --no-cache \
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
    nss \
    mesa-gl \
    dbus \
    glib \
    glib-dev \
    gtk+3.0

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

# Python 및 Selenium 설치
RUN apk add --no-cache python3 py3-pip \
    && pip install selenium

# JAR 파일 복사
ARG JAR_FILE=build/libs/sw-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# JVM 시간대 설정 및 애플리케이션 실행
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]
