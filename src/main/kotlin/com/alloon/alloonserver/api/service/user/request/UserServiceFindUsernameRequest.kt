package com.alloon.alloonserver.api.service.user.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "아이디 찾기 요청 객체")
data class UserServiceFindUsernameRequest(
    @Schema(description = "이메일", example = "photi@photi.com")
    var email: String,
)
