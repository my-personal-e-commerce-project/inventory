FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/inventory-*.jar app.jar

ENV SERVER_PORT=80
EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
