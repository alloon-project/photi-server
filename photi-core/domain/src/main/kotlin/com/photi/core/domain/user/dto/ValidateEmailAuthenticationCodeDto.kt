package com.photi.core.domain.user.dto

data class ValidateEmailAuthenticationCodeDto(
    val email: String,
    val authenticationCode: String,
)
