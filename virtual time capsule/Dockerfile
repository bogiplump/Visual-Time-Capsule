FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./
COPY backend ./backend/
COPY frontend ./frontend/

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

EXPOSE 8080

ARG JAR_FILE

COPY --from=build /app/backend/target/${JAR_FILE} app.jar

COPY ./.env /app/.env

ENTRYPOINT ["java", "-jar", "app.jar"]
