package com.alloon.alloonserver.api.service.user.response

import com.alloon.alloonserver.domain.user.User

data class UserLoginResponse(
    val userId: Long,
    val username: String,
    val imageUrl: String?,
    val isTemporaryPassword: Boolean
) {

    constructor(user: User) : this(user.id!!, user.username, user.imageUrl, user.isTemporaryPassword)
}
