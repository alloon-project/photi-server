package com.photi.server.service.user.dto

import com.photi.server.domain.user.Contact

data class ContactServiceSendVerificationDto(
    val email: String,
) {

    fun toEntity(verificationCode: String): Contact {
        return Contact(email = email, verificationCode = verificationCode)
    }
}
