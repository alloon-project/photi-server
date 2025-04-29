#!/bin/bash
DOCKER_APP_NAME=spring-photi
PROJECT_PATH=/home/ubuntu/photi-server

EXIST_RUNNING=$(docker-compose -p "${DOCKER_APP_NAME}" -f $PROJECT_PATH/docker-compose.yml ps | grep -E "Up|running")

echo "deploy start - $(date "+%Y-%m-%d %H:%M:%S")"

if [ -z "$EXIST_RUNNING" ]; then
  docker-compose -p "${DOCKER_APP_NAME}" -f $PROJECT_PATH/docker-compose.yml up -d --build
  docker image prune -af
fi
  echo "deploy end - $(date '+%Y-%m-%d %H:%M:%S')"
