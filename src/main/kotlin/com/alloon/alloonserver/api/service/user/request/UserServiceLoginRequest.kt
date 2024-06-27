package com.alloon.alloonserver.api.service.user.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청 객체")
data class UserServiceLoginRequest(
    @Schema(description = "아이디", example = "photi")
    var username: String,

    @Schema(description = "비밀번호", example = "password1!")
    var password: String,
)