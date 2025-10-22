package com.photi.core.domain.challenge.service

import com.photi.core.domain.challenge.dto.ChallengeHashtagDto
import com.photi.core.domain.challenge.model.ChallengeHashtag
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class HashtagService(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    @Async
    fun addHashtags(hashtags: List<ChallengeHashtagDto>) {
        hashtags.map { it.hashtag }
            .forEach { hashtag ->
                redisTemplate.opsForZSet().incrementScore(POPULAR_HASHTAGS_KEY, hashtag, 1.0)
            }
    }

    @Async
    fun changeHashtags(hashtags: List<ChallengeHashtagDto>) {
        hashtags.map { it.hashtag }
            .forEach { hashtag ->
                val score = redisTemplate.opsForZSet().score(POPULAR_HASHTAGS_KEY, hashtag)
                if (score == null) {
                    redisTemplate.opsForZSet().incrementScore(POPULAR_HASHTAGS_KEY, hashtag, 1.0)
                }
            }
    }

    @Async
    fun deleteHashtags(hashtags: List<ChallengeHashtagDto>) {
        hashtags.map { it.hashtag }
            .forEach { hashtag ->
                val score = redisTemplate.opsForZSet().score(POPULAR_HASHTAGS_KEY, hashtag)
                if (score != null) {
                    redisTemplate.opsForZSet().incrementScore(POPULAR_HASHTAGS_KEY, hashtag, -1.0)
                    if (score <= 0) {
                        redisTemplate.opsForZSet().remove(POPULAR_HASHTAGS_KEY, hashtag)
                    }
                }
            }
    }

    fun findPopularChallengeHashtags(): Set<String> {
        return redisTemplate.opsForZSet()
            .reverseRange(POPULAR_HASHTAGS_KEY, 0, 9) ?: emptySet()
    }

    companion object {
        private const val POPULAR_HASHTAGS_KEY = "popular:hashtags"
    }
}
