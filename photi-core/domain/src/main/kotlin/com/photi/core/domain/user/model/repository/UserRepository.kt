package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, UserCustomRepository {

    fun existsByEmailAndDeletedFalse(email: String): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun findByEmailAndAuthenticatedTrue(email: String): User?

    fun findByEmailAndUsernameAndAuthenticatedTrue(email: String, username: String): User?
}
