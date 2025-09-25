package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.UserRole
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<UserRole, Long>, UserRoleCustomRepository
