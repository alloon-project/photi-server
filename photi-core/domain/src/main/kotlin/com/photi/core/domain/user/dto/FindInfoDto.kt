package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User
import com.querydsl.core.annotations.QueryProjection

data class FindInfoDto @QueryProjection constructor(
    val imageUrl: String,
    val username: String,
    val email: String,
) {

    companion object {

        fun of(user: User) = FindInfoDto(user.imageUrl!!, user.username!!, user.email)
    }
}
