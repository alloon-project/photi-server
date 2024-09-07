package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.ChallengeHashtagDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 해시태그 응답 객체")
data class ChallengeHashtagResponse(

    @Schema(description = "챌린지 해시태그 내용")
    val hashtag: String,
) {

    companion object {

        fun of(challengeHashtag: ChallengeHashtagDto): ChallengeHashtagResponse {
            return ChallengeHashtagResponse(challengeHashtag.hashtag)
        }

        fun of(challengeHashtags: List<ChallengeHashtagDto>): List<ChallengeHashtagResponse> {
            return challengeHashtags.map { of(it) }
        }
    }
}