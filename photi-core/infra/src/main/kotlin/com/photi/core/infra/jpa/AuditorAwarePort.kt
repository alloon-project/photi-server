package com.photi.core.infra.jpa

interface AuditorAwarePort {

    fun getCurrentAuditor(): String?
}
