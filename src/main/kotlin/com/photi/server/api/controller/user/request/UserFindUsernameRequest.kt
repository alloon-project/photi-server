package com.photi.server.api.controller.user.request

import com.photi.server.service.user.dto.UserServiceFindUsernameDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "아이디 찾기 요청 객체")
data class UserFindUsernameRequest(

    @Schema(description = "이메일", example = "photi@photi.com")
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    val email: String,
) {

    fun toServiceDto(): UserServiceFindUsernameDto {
        return UserServiceFindUsernameDto(email)
    }
}
