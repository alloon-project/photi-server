package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.Contact

data class ContactServiceSendVerificationDto(
    val email: String,
) {

    fun toEntity(verificationCode: String) =
        Contact(email = email, verificationCode = verificationCode)
}
