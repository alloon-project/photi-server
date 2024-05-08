package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.api.service.user.request.ContactServiceVerifyRequest
import jakarta.validation.constraints.NotBlank

data class ContactVerifyRequest(
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    var email: String?,

    @field:NotBlank(message = "이메일 인증코드는 필수 입력입니다.")
    var verificationCode: String?,
) {
    fun toServiceRequest(): ContactServiceVerifyRequest {
        return ContactServiceVerifyRequest(email!!, verificationCode!!)
    }
}
