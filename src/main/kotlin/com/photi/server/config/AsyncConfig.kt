package com.photi.server.config

import com.photi.server.common.exception.CustomAsyncUncaughtExceptionHandler
import com.photi.server.common.log.LoggingTaskDecorator
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@EnableAsync
@Configuration
class AsyncConfig : AsyncConfigurer {

    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            queueCapacity = 50
            maxPoolSize = 30
            keepAliveSeconds = 60
            setThreadNamePrefix("Executor-")
            setTaskDecorator(LoggingTaskDecorator())
            setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(20)
            setAcceptTasksAfterContextClose(false)
            setAllowCoreThreadTimeOut(false)
            initialize()
        }
    }

    override fun getAsyncExecutor(): Executor {
        return taskExecutor()
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return CustomAsyncUncaughtExceptionHandler()
    }
}