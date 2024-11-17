package com.alloon.alloonserver.api.controller.user.request

import com.alloon.alloonserver.service.user.dto.DeleteUserDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "회원 탈퇴 요청 객체")
data class DeleteUserRequest(

    @Schema(description = "기존 비밀번호", example = "password1!")
    @field:NotBlank(message = "비밀번호는 필수 업력입니다.")
    val password: String,
) {

    fun toServiceDto(): DeleteUserDto {
        return DeleteUserDto(password)
    }
}
