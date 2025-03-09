package com.photi.server.api.controller.user.request

import com.photi.server.service.user.dto.UserServiceLoginDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 요청 객체")
data class UserLoginRequest(

    @Schema(description = "아이디", example = "photi")
    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    val username: String,

    @Schema(description = "비밀번호", example = "password1!")
    @field:NotBlank(message = "비밀번호는 필수 입력입니다.")
    val password: String,
) {

    fun toServiceDto(): UserServiceLoginDto {
        return UserServiceLoginDto(username, password)
    }
}
