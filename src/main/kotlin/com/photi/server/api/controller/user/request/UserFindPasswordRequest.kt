package com.photi.server.api.controller.user.request

import com.photi.server.service.user.dto.UserServiceFindPasswordDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "비밀번호 찾기 요청 객체")
data class UserFindPasswordRequest(

    @Schema(description = "이메일", example = "photi@photi.com")
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    val email: String,

    @Schema(description = "아이디", example = "photi")
    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    val username: String,
) {

    fun toServiceDto(): UserServiceFindPasswordDto {
        return UserServiceFindPasswordDto(email, username)
    }
}
