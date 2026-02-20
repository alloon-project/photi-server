package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class OAuthLoginDto(
    val userId: Long,
    val username: String?,
) {

    companion object {

        fun of(user: User) = OAuthLoginDto(user.id!!, user.username)
    }
}
