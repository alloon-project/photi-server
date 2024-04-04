package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.api.service.user.request.UserServiceChangePasswordRequest
import jakarta.validation.constraints.NotBlank

data class UserChangePasswordRequest(
    @field:NotBlank(message = "비밀번호는 필수 업력입니다.")
    var password: String,
    @field:NotBlank(message = "새 비밀번호는 필수 입력입니다.")
    var newPassword: String,
    @field:NotBlank(message = "새 비밀번호 재입력은 필수 입력입니다.")
    var newPasswordReEntered: String,
) {
    fun toServiceRequest(): UserServiceChangePasswordRequest {
        return UserServiceChangePasswordRequest(password, newPassword, newPasswordReEntered)
    }
}
