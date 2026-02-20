package com.photi.apis.enduser.controller.challenge.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 인증 피드 존재 여부 조회 응답 객체")
data class FindChallengeHasFeedResponse(

    @Schema(description = "인증 피드 존재 여부", example = "true")
    val hasFeed: Boolean,
) {

    companion object {

        fun of(hasFeed: Boolean) = FindChallengeHasFeedResponse(hasFeed)
    }
}
