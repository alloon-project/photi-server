package com.alloon.alloonserver.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface UserRepository : JpaRepository<User, BigInteger> {

    fun existsByContact(contact: Contact): Boolean
}