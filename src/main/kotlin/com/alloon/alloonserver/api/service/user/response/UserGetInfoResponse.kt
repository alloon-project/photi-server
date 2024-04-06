package com.alloon.alloonserver.api.service.user.response

import com.alloon.alloonserver.domain.user.User

data class UserGetInfoResponse(
    val userId: Long,
    val username: String,
    val imageUrl: String?,
    val email: String,
) {

    constructor(user: User) : this(user.id!!, user.username, user.imageUrl, user.contact.email)
}
