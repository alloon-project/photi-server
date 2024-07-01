package com.alloon.alloonserver.api.service.user.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "비밀번호 찾기 요청 객체")
data class UserServiceFindPasswordRequest(
    @Schema(description = "이메일", example = "photi@photi.com")
    var email: String,

    @Schema(description = "아이디", example = "photi")
    var username: String,
)
