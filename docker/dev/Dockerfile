FROM eclipse-temurin:17-jdk
ARG JAR_FILE=app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]