package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User
import com.photi.utils.CodeUtil.getAuthenticationCode

data class SendEmailAuthenticationCodeDto(
    val email: String,
) {

    fun toUserEntity() = User(email = email, authenticationCode = getAuthenticationCode())
}
