#!/bin/bash
set -e

echo "[INFO] Waiting for Redis to start..."
until redis-cli ping | grep -q "PONG"; do
  echo "[INFO] Redis is not ready yet. Waiting..."
  sleep 1
done

echo "[INFO] Redis is up. Initializing data..."

declare -A hashtags=(
  ["러닝"]=10
  ["게임"]=20
  ["건강식"]=15
  ["챌린지"]=5
  ["개발"]=7
  ["코틀린"]=9
  ["iOS"]=25
  ["안드로이드"]=15
  ["스프링"]=50
  ["디자인"]=36
)

for tag in "${!hashtags[@]}"; do
  score="${hashtags[$tag]}"
  redis-cli ZADD popular:hashtags "$score" "$tag"
done

echo "[INFO] Initialized hashtags:"
redis-cli ZRANGE popular:hashtags 0 -1 WITHSCORES

echo "[INFO] Redis initialization completed successfully"
