# Этап 1: Сборка
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /workspace

# Копируем ВЕСЬ проект
COPY . .

# Собираем только faceit_analyzer модуль
RUN mvn clean package -DskipTests -pl faceit_analyzer -am

# Этап 2: Продакшен
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Безопасность
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем собранный JAR
COPY --from=builder --chown=spring:spring /workspace/faceit_analyzer/target/*.jar app.jar

# JVM оптимизации
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_PROFILES_ACTIVE="production"

# Порт
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Запуск
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]