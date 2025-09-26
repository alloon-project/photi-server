package com.photi.core.infra.async

import com.photi.core.infra.PhotiConfig
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer, PhotiConfig {

    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            queueCapacity = 50
            maxPoolSize = 30
            keepAliveSeconds = 60
            setThreadNamePrefix("Executor-")
            setTaskDecorator(CustomTaskDecorator())
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
