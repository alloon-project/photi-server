package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.ChallengeRuleDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 인증 룰 응답 객체")
data class ChallengeRuleResponse(

    @Schema(description = "챌린지 인증 룰 내용")
    val rule: String,
) {

    companion object {

        fun of(challengeRule: ChallengeRuleDto): ChallengeRuleResponse {
            return ChallengeRuleResponse(challengeRule.rule)
        }
    }
}