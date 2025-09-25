package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class UserRegisterDto(
    val id: Long,
    val username: String,
) {

    companion object {

        fun of(user: User) = UserRegisterDto(user.id!!, user.username)
    }
}
