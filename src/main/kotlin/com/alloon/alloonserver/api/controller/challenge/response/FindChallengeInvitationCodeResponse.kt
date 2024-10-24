package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.FindChallengeInvitationCodeDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 초대코드 조회 응답 객체")
data class FindChallengeInvitationCodeResponse(

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 초대코드", example = "478DS")
    val invitationCode: String,
) {

    companion object {

        fun of(challenge: FindChallengeInvitationCodeDto): FindChallengeInvitationCodeResponse {
            return FindChallengeInvitationCodeResponse(challenge.name, challenge.invitationCode)
        }
    }
}