package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.api.service.user.request.UserServiceLoginRequest
import jakarta.validation.constraints.NotBlank

data class UserLoginRequest(
    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    var username: String,
    @field:NotBlank(message = "비밀번호는 필수 입력입니다.")
    var password: String,
) {

    fun toServiceRequest(): UserServiceLoginRequest {
        return UserServiceLoginRequest(username, password)
    }
}
