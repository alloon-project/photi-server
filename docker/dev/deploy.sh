#!/bin/bash
DOCKER_APP_NAME=spring-photi
PROJECT_PATH=/home/ubuntu/photi-server

echo "deploy start - $(date "+%Y-%m-%d %H:%M:%S")"

docker compose -p "${DOCKER_APP_NAME}" -f $PROJECT_PATH/docker-compose.yml up -d
docker image prune -af

echo "deploy end - $(date '+%Y-%m-%d %H:%M:%S')"
