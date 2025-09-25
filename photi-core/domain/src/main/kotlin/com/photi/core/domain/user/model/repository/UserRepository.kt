package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.Contact
import com.photi.core.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, UserCustomRepository {

    fun existsByContactAndIsDeletedFalse(contact: Contact): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?
}
