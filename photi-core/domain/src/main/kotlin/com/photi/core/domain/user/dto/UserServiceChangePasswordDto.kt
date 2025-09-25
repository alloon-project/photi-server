package com.photi.core.domain.user.dto

data class UserServiceChangePasswordDto(
    val password: String,
    val newPassword: String,
    val newPasswordReEnter: String,
)
