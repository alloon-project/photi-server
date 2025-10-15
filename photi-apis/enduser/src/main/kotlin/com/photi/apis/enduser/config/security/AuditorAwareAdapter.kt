package com.photi.apis.enduser.config.security

import com.photi.core.infra.jpa.AuditorAwarePort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuditorAwareAdapter : AuditorAwarePort {

    override fun getCurrentAuditor(): String? {
        // todo 수정 필요
        return SecurityContextHolder.getContext().authentication
            ?.takeIf { it.isAuthenticated }
            ?.principal?.toString()
    }
}
