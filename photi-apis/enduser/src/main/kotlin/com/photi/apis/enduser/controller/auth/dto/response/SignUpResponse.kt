package com.photi.apis.enduser.controller.auth.dto.response

import com.photi.core.domain.user.dto.SignUpDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 응답 객체")
data class SignUpResponse(

    @Schema(description = "사용자 식별자", example = "1")
    val userId: Long,

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,
) {

    companion object {

        fun of(user: SignUpDto) = SignUpResponse(user.id, user.username)
    }
}
