package com.alloon.alloonserver.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepository : JpaRepository<Contact, Long> {

    fun findByEmail(email: String): Contact?
}