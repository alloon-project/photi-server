package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.UserRole

interface UserRoleCustomRepository {

    fun findAllFetchUser(userId: Long): List<UserRole>

}