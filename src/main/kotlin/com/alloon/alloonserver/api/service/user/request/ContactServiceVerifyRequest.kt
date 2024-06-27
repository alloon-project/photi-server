package com.alloon.alloonserver.api.service.user.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

@Schema(description = "이메일 인증코드 확인 요청 객체")
data class ContactServiceVerifyRequest(
    @field:Size(min = 1, max = 100, message = "이메일은 1~100자만 가능합니다.")
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    @Schema(description = "이메일", example = "photi@photi.com")
    var email: String,

    @Schema(description = "이메일 인증코드", example = "111111")
    var verificationCode: String,
)
