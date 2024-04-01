package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.user.custom.UserRoleCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRole, Long>, UserRoleCustomRepository {
}