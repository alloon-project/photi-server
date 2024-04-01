package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.user.custom.UserCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, UserCustomRepository {

    fun existsByContact(contact: Contact): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?
}