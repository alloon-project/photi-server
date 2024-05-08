package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.api.service.user.request.UserServiceFindPasswordRequest
import jakarta.validation.constraints.NotBlank

data class UserFindPasswordRequest(
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    var email: String?,

    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    var username: String?,
) {

    fun toServiceRequest(): UserServiceFindPasswordRequest {
        return UserServiceFindPasswordRequest(email!!, username!!)
    }
}
