FROM openjdk:17-jdk-slim AS build
WORKDIR /app
# Copy Gradle wrapper first for caching
COPY gradlew .
COPY gradle/ gradle/
RUN chmod +x gradlew
# Copy source and build
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# 2. Runtime stage: run the packaged JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
VOLUME /tmp

# Copy all built jars into /app/libs
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# Expose application port
EXPOSE 8080

# Run the jar via shell wildcard to support any jar name
CMD ["java", "-jar", "app.jar"]