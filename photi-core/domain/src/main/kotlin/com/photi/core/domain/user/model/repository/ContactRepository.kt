package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.Contact
import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepository : JpaRepository<Contact, Long> {

    fun findByEmail(email: String): Contact?
}
