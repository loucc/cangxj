# ============ Stage 1: Build ============
FROM gradle:9.6-jdk25 AS builder

WORKDIR /build

# 复制 Gradle Wrapper 和构建脚本，利用 Docker 层缓存下载依赖
COPY gradle ./gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew \
    && ./gradlew dependencies --write-locks || true

# 复制各模块源码并打包
COPY cxj-common ./cxj-common
COPY cxj-modules ./cxj-modules
COPY cxj-admin ./cxj-admin
COPY cxj-generator ./cxj-generator
RUN ./gradlew :cxj-admin:bootJar -x test \
    && mv cxj-admin/build/libs/cxj.jar /build/app.jar

# ============ Stage 2: Runtime ============
FROM eclipse-temurin:25-jre-alpine

# 时区与基础工具（curl 用于容器内健康检查）
RUN apk add --no-cache tzdata curl \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && addgroup -S app && adduser -S -G app app

WORKDIR /app

COPY --from=builder --chown=app:app /build/app.jar app.jar

USER app

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseZGC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE="docker"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -fsS http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
