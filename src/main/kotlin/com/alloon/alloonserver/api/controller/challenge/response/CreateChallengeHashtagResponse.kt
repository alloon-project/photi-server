package com.alloon.alloonserver.api.controller.challenge.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 해시태그 응답 객체")
data class CreateChallengeHashtagResponse(

    @Schema(description = "챌린지 해시태그 내용")
    val hashtag: String,
) {

    companion object {

        fun of(hashtag: String): CreateChallengeHashtagResponse {
            return CreateChallengeHashtagResponse(hashtag)
        }
    }
}