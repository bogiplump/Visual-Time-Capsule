FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

# Maven will build the JAR inside the 'backend/target' directory
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

# CORRECTED LINE: Use the exact JAR name from your build output
COPY --from=build /app/backend/target/virtual-time-capsule-backend-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]