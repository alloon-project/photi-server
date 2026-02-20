package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class SignUpRequestDto(
    val email: String,
    val username: String,
    val password: String,
)

data class SignUpDto(
    val id: Long,
    val username: String,
) {

    companion object {

        fun of(user: User) = SignUpDto(user.id!!, user.username!!)
    }
}
