package com.alloon.alloonserver.api.controller.challenge.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 인증 룰")
data class CreateChallengeRuleRequest(

    @field:Size(min = 1, max = 30, message = "인증 룰은 1~30자만 가능합니다.")
    val rule: String,
)