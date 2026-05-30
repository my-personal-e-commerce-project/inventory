FROM maven:3.9.11-eclipse-temurin-21-alpine

ENV SERVER_PORT=80

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests && \
    cp target/inventory-*.jar app.jar

EXPOSE 80 

ENTRYPOINT ["java", "-jar", "app.jar"]
