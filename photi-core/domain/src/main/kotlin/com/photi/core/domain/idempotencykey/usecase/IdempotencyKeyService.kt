package com.photi.core.domain.idempotencykey.usecase

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class IdempotencyKeyService(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    fun validateDuplicatedRequest(key: String, exceptionCode: ExceptionCode) {
        val idempotencyKey = UUID.randomUUID().toString()
        val isFirstRequest = redisTemplate.opsForValue()
            .setIfAbsent(key, idempotencyKey, 2, TimeUnit.SECONDS)

        if (isFirstRequest != true) {
            throw CustomException(exceptionCode)
        }
    }
}
