package com.photi.apis.enduser.controller.challenge.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "해시태그 리스트 조회 응답 객체")
data class FindPopularChallengeHashtagsResponse(

    @Schema(description = "챌린지 해시태그", example = "러닝")
    val hashtag: String,
) {

    companion object {

        fun of(hashtags: Set<String>) = hashtags.map { FindPopularChallengeHashtagsResponse(it) }
    }
}
