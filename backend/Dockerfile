# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Install wget for health checks
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better Docker layer caching)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Give execute permission to mvnw
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose the port that the app runs on
EXPOSE 9193

# Run the jar file
ENTRYPOINT ["java", "-jar", "target/universal-pet-care-0.0.1-SNAPSHOT.jar"]