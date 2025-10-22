package com.photi.apis.enduser.controller.auth.dto.request

import com.photi.core.domain.user.dto.ValidateEmailAuthenticationCodeDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "이메일 인증코드 검증 요청 객체")
data class ValidateEmailAuthenticationCodeRequest(

    @Schema(description = "이메일", example = "photi@photi.com")
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    @field:Size(min = 1, max = 100, message = "이메일은 1~100자만 가능합니다.")
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    val email: String,

    @Schema(description = "이메일 인증코드", example = "1111")
    @field:NotBlank(message = "이메일 인증코드는 필수 입력입니다.")
    val verificationCode: String,
) {

    fun toServiceDto() = ValidateEmailAuthenticationCodeDto(email, verificationCode)
}
