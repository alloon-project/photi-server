package com.alloon.alloonserver.api.service.user.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UserServiceFindUsernameRequest(
    @field:Size(min = 1, max = 100, message = "이메일은 1~100자만 가능합니다.")
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    var email: String,
)
