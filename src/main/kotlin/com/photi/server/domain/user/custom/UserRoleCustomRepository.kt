package com.photi.server.domain.user.custom

import com.photi.server.domain.user.UserRole

interface UserRoleCustomRepository {

    fun findAllFetchUser(userId: Long): List<UserRole>

}