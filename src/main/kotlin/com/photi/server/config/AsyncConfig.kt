package com.photi.server.config

import com.photi.server.common.exception.CustomAsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class AsyncConfig : AsyncConfigurer {

    override fun getAsyncExecutor(): Executor? {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 3
            maxPoolSize = 10
            queueCapacity = 500
            setThreadNamePrefix("Executor-")
            initialize()
        }
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return CustomAsyncUncaughtExceptionHandler()
    }
}