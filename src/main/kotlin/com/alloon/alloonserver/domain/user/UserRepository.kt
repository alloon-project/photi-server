package com.alloon.alloonserver.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByContact(contact: Contact): Boolean

    fun existsByUsername(username: String): Boolean
}