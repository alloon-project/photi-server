package com.photi.server.domain.user

import com.photi.server.domain.user.custom.UserCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, UserCustomRepository {

    fun existsByContactAndIsDeletedFalse(contact: Contact): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?
}