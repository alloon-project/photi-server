package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class FindInfoDto(
    val imageUrl: String,
    val username: String,
    val email: String,
) {

    companion object {

        fun of(user: User) = FindInfoDto(user.imageUrl!!, user.username!!, user.email)
    }
}
