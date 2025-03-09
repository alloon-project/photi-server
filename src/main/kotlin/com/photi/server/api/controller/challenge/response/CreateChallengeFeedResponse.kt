package com.photi.server.api.controller.challenge.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 피드 인증 응답 객체")
data class CreateChallengeFeedResponse(

    @Schema(description = "피드 id", example = "1")
    val id: Long?,
) {

    companion object {

        fun of(id: Long?): CreateChallengeFeedResponse = CreateChallengeFeedResponse(id)
    }
}
