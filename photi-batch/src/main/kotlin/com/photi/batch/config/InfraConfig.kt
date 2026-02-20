package com.photi.batch.config

import com.photi.core.infra.EnablePhotiConfig
import com.photi.core.infra.PhotiConfigGroup
import org.springframework.context.annotation.Configuration

@Configuration
@EnablePhotiConfig([PhotiConfigGroup.JPA])
class InfraConfig
