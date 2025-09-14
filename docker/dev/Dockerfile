FROM eclipse-temurin:17-jdk
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:UseContainerCpuShares=disabled"
ENTRYPOINT ["java", "-jar", "app.jar"]