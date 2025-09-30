package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class LoginRequestDto(
    val username: String,
    val password: String,
)

data class LoginDto(
    val id: Long,
    val username: String,
    val imageUrl: String?,
    val isTemporaryPassword: Boolean,
) {

    companion object {

        fun of(user: User) = LoginDto(
            user.id!!,
            user.username!!,
            user.imageUrl,
            user.isTemporaryPassword,
        )
    }
}
