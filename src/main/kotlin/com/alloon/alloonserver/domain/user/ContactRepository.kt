package com.alloon.alloonserver.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface ContactRepository : JpaRepository<Contact, BigInteger> {

    fun findByEmail(email: String): Contact?
}