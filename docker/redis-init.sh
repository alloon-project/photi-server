#!/bin/bash
echo "Waiting for Redis to start..."
until redis-cli ping | grep -q "PONG"; do
  echo "Redis is not ready yet. Waiting..."
  sleep 1
done

echo "Redis initialization..."
redis-cli <<EOF
ZADD popular:hashtags 10 "러닝"
ZADD popular:hashtags 20 "게임"
ZADD popular:hashtags 15 "건강식"
ZADD popular:hashtags 5 "챌린지"
ZADD popular:hashtags 7 "개발"
ZADD popular:hashtags 9 "코틀린"
ZADD popular:hashtags 25 "iOS"
ZADD popular:hashtags 15 "안드로이드"
ZADD popular:hashtags 50 "스프링"
ZADD popular:hashtags 36 "디자인"
ZRANGE popular:hashtags 0 -1 WITHSCORES
EOF
echo "Redis initialization completed."
