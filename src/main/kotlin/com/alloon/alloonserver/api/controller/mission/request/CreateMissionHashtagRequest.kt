package com.alloon.alloonserver.api.controller.mission.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "챌린지 해시태그")
data class CreateMissionHashtagRequest(

    @field:Size(min = 1, max = 6, message = "해시태그는 1~6자만 가능합니다.")
    val hashtag: String,
)