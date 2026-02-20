package com.photi.core.domain.user.dto

data class ChangePasswordDto(
    val password: String,
    val newPassword: String,
    val reEnteredNewPassword: String,
)
