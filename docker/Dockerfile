# Builder stage
FROM eclipse-temurin:17 as builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test --no-daemon

# Package stage
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java", "-jar", "app.jar"]