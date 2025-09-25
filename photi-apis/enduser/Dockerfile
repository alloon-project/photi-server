FROM eclipse-temurin:17 AS builder
WORKDIR /workspace
COPY . .
RUN ./gradlew :photi-apis:enduser:clean :photi-apis:enduser:build -x test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /workspace/photi-apis/enduser/build/libs/*.jar /app/enduser.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "enduser.jar"]
