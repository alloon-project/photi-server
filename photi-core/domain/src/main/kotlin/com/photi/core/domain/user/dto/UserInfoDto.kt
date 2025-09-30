package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User
import com.querydsl.core.annotations.QueryProjection

data class UserInfoDto @QueryProjection constructor(
    val imageUrl: String,
    val username: String,
    val email: String,
) {

    companion object {

        fun of(user: User) = UserInfoDto(user.imageUrl!!, user.username!!, user.email)
    }
}
