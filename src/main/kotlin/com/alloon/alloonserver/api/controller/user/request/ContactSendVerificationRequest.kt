package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.api.service.user.request.ContactServiceSendVerificationRequest
import jakarta.validation.constraints.NotBlank

data class ContactSendVerificationRequest(
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    var email: String,
) {

    fun toServiceRequest(): ContactServiceSendVerificationRequest {
        return ContactServiceSendVerificationRequest(email)
    }
}