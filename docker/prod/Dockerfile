FROM eclipse-temurin:17-jdk
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]