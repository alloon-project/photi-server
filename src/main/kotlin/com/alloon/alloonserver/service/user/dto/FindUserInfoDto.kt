package com.alloon.alloonserver.service.user.dto

import com.alloon.alloonserver.domain.user.User
import com.querydsl.core.annotations.QueryProjection

data class FindUserInfoDto @QueryProjection constructor(
    val imageUrl: String,
    val username: String,
    val email: String,
) {

    companion object {

        fun of(user: User): FindUserInfoDto {
            return FindUserInfoDto(user.imageUrl, user.username, user.contact.email)
        }
    }
}