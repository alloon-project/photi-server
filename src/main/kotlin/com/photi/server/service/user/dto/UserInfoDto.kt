package com.photi.server.service.user.dto

import com.photi.server.domain.user.User
import com.querydsl.core.annotations.QueryProjection

data class UserInfoDto @QueryProjection constructor(
    val imageUrl: String,
    val username: String,
    val email: String,
) {

    companion object {

        fun of(user: User): UserInfoDto {
            return UserInfoDto(user.imageUrl, user.username, user.contact.email)
        }
    }
}