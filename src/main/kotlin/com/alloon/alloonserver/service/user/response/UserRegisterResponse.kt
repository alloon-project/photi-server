package com.alloon.alloonserver.service.user.response

import com.alloon.alloonserver.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 응답 객체")
data class UserRegisterResponse(
    @Schema(description = "사용자 식별자", example = "1")
    val userId: Long,
    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,
) {

    constructor(user: User) : this(user.id!!, user.username)
}