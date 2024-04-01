package com.alloon.alloonserver.api.service.user.response

import com.alloon.alloonserver.domain.user.User

data class UserRegisterResponse(
    val userId: Long,
    val username: String,
) {

    constructor(user: User) : this(user.id!!, user.username)
}