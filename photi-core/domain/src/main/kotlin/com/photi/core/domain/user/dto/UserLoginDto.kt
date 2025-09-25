package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class UserLoginDto(
    val id: Long,
    val username: String,
    val imageUrl: String,
    val isTemporaryPassword: Boolean,
) {

    companion object {

        fun of(user: User) = UserLoginDto(
            user.id!!,
            user.username,
            user.imageUrl,
            user.temporaryPasswordYn,
        )
    }
}
