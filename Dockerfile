# Imagen con Java 17 y Maven para compilar y ejecutar NexusMarket dentro de Docker.
FROM maven:3.9-eclipse-temurin-17

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY src ./src

EXPOSE 8080

CMD ["mvn", "-B", "spring-boot:run"]
