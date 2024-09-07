package com.alloon.alloonserver.api.controller.challenge.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 해시태그 요청 객체")
data class CreateChallengeHashtagRequest(

    @field:NotBlank(message = "해시태그는 필수 입력입니다.")
    @field:Size(min = 1, max = 6, message = "해시태그는 1~6자만 가능합니다.")
    val hashtag: String,
)