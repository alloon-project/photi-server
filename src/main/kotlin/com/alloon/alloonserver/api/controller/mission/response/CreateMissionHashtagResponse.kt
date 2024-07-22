package com.alloon.alloonserver.api.controller.mission.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 해시태그 응답 객체")
data class CreateMissionHashtagResponse(

    @Schema(description = "챌린지 해시태그 내용")
    val hashtag: String,
) {
}