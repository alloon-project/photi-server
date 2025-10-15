package com.photi.apis.enduser.controller.challenge.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 초대코드 일치 여부 조회 응답 객체")
data class FindChallengeInvitationCodeMatchesResponse(

    @Schema(description = "초대코드 일치 여부", example = "true")
    val isMatch: Boolean,
) {

    companion object {

        fun of(isMatch: Boolean) = FindChallengeInvitationCodeMatchesResponse(isMatch)
    }
}
