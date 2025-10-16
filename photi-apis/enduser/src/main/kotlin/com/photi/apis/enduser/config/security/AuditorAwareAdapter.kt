package com.photi.apis.enduser.config.security

import com.photi.core.infra.jpa.AuditorAwarePort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuditorAwareAdapter : AuditorAwarePort {

    override fun getCurrentAuditor(): String? {
        return SecurityContextHolder.getContext().authentication
            ?.takeIf { it.isAuthenticated }
            ?.principal
            ?.let { getUserId(it) }
    }

    private fun getUserId(principal: Any): String? {
        if (principal is CustomUserDetails) {
            return principal.username
        }
        return null
    }
}
