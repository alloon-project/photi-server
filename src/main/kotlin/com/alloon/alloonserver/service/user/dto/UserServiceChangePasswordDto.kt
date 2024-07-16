package com.alloon.alloonserver.service.user.dto

data class UserServiceChangePasswordDto(
    val password: String,
    val newPassword: String,
    val newPasswordReEnter: String,
)
