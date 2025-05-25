# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

RUN mvn clean install

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/virtual-time-capsule-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
