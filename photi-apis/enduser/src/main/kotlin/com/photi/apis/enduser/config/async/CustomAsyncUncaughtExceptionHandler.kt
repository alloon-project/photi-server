package com.photi.apis.enduser.config.async

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

class CustomAsyncUncaughtExceptionHandler : AsyncUncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger(CustomAsyncUncaughtExceptionHandler::class.java)

    override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any?) {
        logger.error("Exception occurred in async method: ${method.name}", ex.message)
        if (params.isNotEmpty()) {
            logger.error("Method parameters: ${params.joinToString()}")
        }
    }
}
