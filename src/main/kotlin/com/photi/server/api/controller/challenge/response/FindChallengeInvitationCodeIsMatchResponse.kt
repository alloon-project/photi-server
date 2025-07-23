package com.photi.server.api.controller.challenge.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 초대코드 일치 여부 조회 응답 객체")
data class FindChallengeInvitationCodeIsMatchResponse(

    @Schema(description = "초대코드 일치 여부", example = "true")
    val isMatch: Boolean,
) {

    companion object {

        fun of(isMatch: Boolean): FindChallengeInvitationCodeIsMatchResponse {
            return FindChallengeInvitationCodeIsMatchResponse(isMatch)
        }
    }
}