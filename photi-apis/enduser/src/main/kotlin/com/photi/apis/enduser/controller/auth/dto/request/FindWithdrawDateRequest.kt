package com.photi.apis.enduser.controller.auth.dto.request

import com.photi.core.domain.user.dto.FindWithdrawDateRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "회원 탈퇴 날짜 조회 요청 객체")
data class FindWithdrawDateRequest(

    @Schema(description = "이메일", example = "photi@photi.com")
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    @field:Size(min = 1, max = 100, message = "이메일은 1~100자만 가능합니다.")
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    val email: String,
) {

    fun toServiceDto() = FindWithdrawDateRequestDto(email)
}
