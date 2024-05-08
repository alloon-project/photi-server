package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.api.service.user.request.UserServiceRegisterRequest
import jakarta.validation.constraints.NotBlank

data class UserRegisterRequest(
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    var email: String?,
    @field:NotBlank(message = "인증코드는 필수 입력입니다.")
    var verificationCode: String?,
    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    var username: String?,
    @field:NotBlank(message = "비밀번호는 필수 입력입니다.")
    var password: String?,
    @field:NotBlank(message = "비밀번호 재입력은 필수 입력입니다.")
    var passwordReEnter: String?,
) {
    fun toServiceRequest(): UserServiceRegisterRequest {
        return UserServiceRegisterRequest(email!!, verificationCode!!, username!!, password!!, passwordReEnter!!)
    }
}
