FROM openjdk:17-alpine

ENV TZ=Asia/Seoul

RUN apk update && \
    apk upgrade && \
    rm -rf /var/cache/apk/*

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]