package com.photi.server.domain.user

import com.photi.server.domain.user.custom.UserRoleCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRole, Long>, UserRoleCustomRepository {
}