package com.photi.server.api.controller.challenge.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 인증 룰 요청 객체")
data class ChallengeRuleRequest(

    @field:NotBlank(message = "인증 룰은 필수 입력입니다.")
    @field:Size(min = 1, max = 30, message = "인증 룰은 1~30자만 가능합니다.")
    val rule: String,
)