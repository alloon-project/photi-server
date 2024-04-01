package com.alloon.alloonserver.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@EnableAsync
@Configuration
class AsyncConfig : AsyncConfigurer {

    override fun getAsyncExecutor(): Executor =
        ThreadPoolExecutor(10, 30, 30, TimeUnit.SECONDS, LinkedBlockingQueue())
}