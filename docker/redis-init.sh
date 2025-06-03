#!/bin/bash
set -e

echo "[INFO] Initializing Redis data..."

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
  redis-cli -h redis ZADD popular:hashtags "$score" "$tag"
done

echo "[INFO] Initialized hashtags:"
redis-cli -h redis ZRANGE popular:hashtags 0 -1 WITHSCORES

echo "[INFO] Redis initialization completed successfully"
