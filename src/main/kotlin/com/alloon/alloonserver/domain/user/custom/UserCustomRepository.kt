package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.User

interface UserCustomRepository {

    fun findFetchContact(email: String?, username: String?, userId: Long?): User?

    fun find(userId: Long): User?
}