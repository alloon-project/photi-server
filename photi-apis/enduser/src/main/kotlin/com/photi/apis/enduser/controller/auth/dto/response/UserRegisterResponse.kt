package com.photi.apis.enduser.controller.auth.dto.response

import com.photi.core.domain.user.dto.UserRegisterDto
import com.photi.core.domain.user.model.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원가입 응답 객체")
data class UserRegisterResponse(

    @Schema(description = "사용자 식별자", example = "1")
    val userId: Long,

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,
) {

    constructor(user: User) : this(user.id!!, user.username)

    companion object {

        fun of(user: UserRegisterDto) = UserRegisterResponse(
            user.id,
            user.username,
        )
    }
}
