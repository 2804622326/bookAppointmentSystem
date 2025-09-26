# Simple single-stage build for now
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9192
ENTRYPOINT ["java", "-jar", "app.jar"]