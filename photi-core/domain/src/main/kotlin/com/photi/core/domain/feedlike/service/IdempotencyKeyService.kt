package com.photi.core.domain.feedlike.service

import com.photi.core.domain.feedlike.exception.FeedLikeException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class IdempotencyKeyService(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    fun validateFeedLike(userId: Long, feedId: Long) {
        val key = "like:$userId:$feedId"
        val idempotencyKey = UUID.randomUUID().toString()
        val isFirstRequest = redisTemplate.opsForValue()
            .setIfAbsent(key, idempotencyKey, 2, TimeUnit.SECONDS)
        if (isFirstRequest != true) {
            throw FeedLikeException.ExistsFeedLikeException()
        }
    }
}
