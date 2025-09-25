package com.photi.core.domain.user.dto

data class ContactServiceVerifyDto(
    val email: String,
    val verificationCode: String,
)
