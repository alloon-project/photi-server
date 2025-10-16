package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User

data class SendEmailAuthenticationCodeDto(
    val email: String,
) {

    fun toEntity(authenticationCode: String) =
        User(email = email, authenticationCode = authenticationCode)
}
