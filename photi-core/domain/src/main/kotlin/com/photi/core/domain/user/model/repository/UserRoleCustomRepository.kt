package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.UserRole

interface UserRoleCustomRepository {

    fun findAllFetchUser(userId: Long): List<UserRole>
}
