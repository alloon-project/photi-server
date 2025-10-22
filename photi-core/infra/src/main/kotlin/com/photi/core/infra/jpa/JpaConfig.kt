package com.photi.core.infra.jpa

import com.photi.core.infra.PhotiConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
class JpaConfig : PhotiConfig {

    @Bean
    fun securityAuditorAware(auditorAwarePort: AuditorAwarePort): AuditorAware<String> {
        return SecurityAuditorAware(auditorAwarePort)
    }
}
