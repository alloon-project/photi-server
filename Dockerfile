FROM openjdk:17-alpine

ENV TZ=Asia/Seoul

RUN apt-get update && \
    apt-get upgrade -y && \
    rm -rf /var/cache/apk/*

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]