package com.alloon.alloonserver.config

import com.alloon.alloonserver.common.log.P6spyLogging
import com.p6spy.engine.spy.P6SpyOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class P6spyConfig {

    @PostConstruct
    fun logFormat() {
        P6SpyOptions.getActiveInstance().logMessageFormat = P6spyLogging::class.java.name
    }
}