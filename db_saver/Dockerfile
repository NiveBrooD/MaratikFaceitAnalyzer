FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]