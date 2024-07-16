package com.alloon.alloonserver.service.user.dto

import com.alloon.alloonserver.domain.user.Contact

data class ContactServiceSendVerificationDto(
    val email: String,
) {

    fun toEntity(verificationCode: String): Contact {
        return Contact(email = email, verificationCode = verificationCode)
    }
}
