package com.photi.server.service.user.dto

data class ContactServiceVerifyDto(
    val email: String,
    val verificationCode: String,
)
