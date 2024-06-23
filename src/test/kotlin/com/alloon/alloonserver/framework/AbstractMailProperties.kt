package com.alloon.alloonserver.framework

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

public interface AbstractMailProperties {

    companion object {
        @DynamicPropertySource
        fun dynamicRegistry(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            dynamicPropertyRegistry.add("spring.mail.host", {":"})
            dynamicPropertyRegistry.add("spring.mail.port", {8080})
            dynamicPropertyRegistry.add("spring.mail.username", {"username"})
            dynamicPropertyRegistry.add("spring.mail.password", {"password"})
        }
    }
}