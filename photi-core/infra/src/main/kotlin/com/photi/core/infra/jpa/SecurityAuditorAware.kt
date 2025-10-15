package com.photi.core.infra.jpa

import org.springframework.data.domain.AuditorAware
import java.util.*

class SecurityAuditorAware(
    private val auditorAwarePort: AuditorAwarePort,
) : AuditorAware<String> {

    override fun getCurrentAuditor() = Optional.ofNullable(auditorAwarePort.getCurrentAuditor())
}
