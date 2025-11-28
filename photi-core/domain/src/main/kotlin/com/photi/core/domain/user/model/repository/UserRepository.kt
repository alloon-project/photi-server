package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.dto.FindInfoDto
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long>, UserCustomRepository {

    fun existsByEmailAndRole(email: String, role: RoleType): Boolean

    fun existsByUsername(username: String): Boolean

    fun existsByOAuthInfo(oAuthInfo: OAuthInfo): Boolean

    fun findByEmailAndRole(email: String, role: RoleType): User?

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun findByEmailAndIsAuthenticatedTrue(email: String): User?

    fun findByEmailAndUsernameAndIsAuthenticatedTrue(email: String, username: String): User?

    @Query("select new com.photi.core.domain.user.dto.FindInfoDto(u.imageUrl, u.username, u.email) from User u where u.id = :id")
    fun findInfoById(id: Long): FindInfoDto?
}
