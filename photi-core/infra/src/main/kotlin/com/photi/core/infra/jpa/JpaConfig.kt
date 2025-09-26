package com.photi.core.infra.jpa

import com.photi.core.infra.PhotiConfig
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
class JpaConfig : PhotiConfig
