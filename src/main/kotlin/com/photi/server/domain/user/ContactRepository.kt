package com.photi.server.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepository : JpaRepository<Contact, Long> {

    fun findByEmail(email: String): Contact?
}