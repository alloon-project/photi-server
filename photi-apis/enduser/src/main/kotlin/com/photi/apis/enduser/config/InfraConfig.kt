package com.photi.apis.enduser.config

import com.photi.core.infra.EnablePhotiConfig
import com.photi.core.infra.PhotiConfigGroup
import org.springframework.context.annotation.Configuration

@Configuration
@EnablePhotiConfig(
    [
        PhotiConfigGroup.JPA,
        PhotiConfigGroup.QUERYDSL,
        PhotiConfigGroup.REDIS,
        PhotiConfigGroup.S3,
        PhotiConfigGroup.ASYNC,
        PhotiConfigGroup.REDIS_CACHE,
        PhotiConfigGroup.FEIGN,
        PhotiConfigGroup.CONFIGURATION_PROPERTIES,
    ]
)
class InfraConfig
