FROM openjdk:17-jdk-slim
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]