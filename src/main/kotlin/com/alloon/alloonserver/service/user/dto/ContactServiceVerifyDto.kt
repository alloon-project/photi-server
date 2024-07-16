package com.alloon.alloonserver.service.user.dto

data class ContactServiceVerifyDto(
    val email: String,
    val verificationCode: String,
)
