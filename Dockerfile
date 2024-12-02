# Base Image
FROM bellsoft/liberica-openjdk-debian:17

# 시스템 시간대 설정
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y \
    tzdata \
    curl \
    wget \
    unzip \
    libnss3 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxrandr2 \
    libxi6 \
    libatk1.0-0 \
    libxcb1 \
    libstdc++6 \
    mesa-utils \
    && rm -rf /var/lib/apt/lists/* \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" > /etc/timezone

# Google Chrome 다운로드
RUN wget -O /tmp/chrome-linux64.zip https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.85/linux64/chrome-linux64.zip \
    && unzip /tmp/chrome-linux64.zip -d /usr/local/ \
    && mv /usr/local/chrome-linux64 /usr/local/google-chrome \
    && ln -s /usr/local/google-chrome/chrome /usr/bin/google-chrome \
    && rm -rf /tmp/chrome-linux64.zip

# ChromeDriver 다운로드
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
