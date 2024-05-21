package com.alloon.alloonserver.api.service.redis

import com.alloon.alloonserver.common.constant.ExceptionCode.REDIS_LOCK_FORCE_LEASED
import com.alloon.alloonserver.common.constant.ExceptionCode.REDIS_LOCK_WAIT_TIMEOUT
import com.alloon.alloonserver.common.response.CustomException
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisLockService(
    private val redissonClient: RedissonClient,
) {

    /**
     * 레디스 락
     * @param lockName 락명
     * @param task 실행 작업
     * @param waitTime 대기 시간
     * @param leaseTime 대여 시간
     * @exception REDIS_LOCK_WAIT_TIMEOUT 423
     * @exception REDIS_LOCK_FORCE_LEASED 423
     * @return 실행 작업
     */
    fun <R> tryLockWith(lockName: String, task: () -> R, waitTime: Long = 0, leaseTime: Long = 0): R {
        val rLock: RLock = redissonClient.getLock(lockName)
        val available: Boolean = rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)

        if (!available) throw CustomException(REDIS_LOCK_WAIT_TIMEOUT)

        return try {
            task()
        } finally {
            if (!rLock.isHeldByCurrentThread) throw CustomException(REDIS_LOCK_FORCE_LEASED)
            rLock.unlock()
        }
    }
}