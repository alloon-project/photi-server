package com.alloon.alloonserver.api.service.user.request

import com.alloon.alloonserver.domain.user.Contact
import jakarta.validation.constraints.Email

data class ContactServiceSendVerificationRequest(
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    var email: String,
) {

    fun toEntity(verificationCode: String): Contact {
        return Contact(email = email, verificationCode = verificationCode)
    }
}
