# --- STAGE 1: Build the application ---
FROM maven:3.9.5-amazoncorretto-21-al2023 AS build
# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files (pom.xml) first to leverage Docker layer caching.
# If pom.xml doesn't change, this step and the next don't need to re-run.
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application
# We use 'install' to resolve all dependencies and package the application.
RUN mvn clean install -DskipTests

FROM amazoncorretto:21

# Set the argument for the path to the JAR file
ARG JAR_FILE=/app/target/DistributedJobScheduler-0.0.1-SNAPSHOT.jar

# Copy the built JAR file from the 'build' stage
COPY --from=build ${JAR_FILE} app.jar

# Expose the default Spring Boot port (only for documentation/tools)
EXPOSE 8080

# Define the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]