package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.domain.challenge.ChallengeRule
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 인증 룰 응답 객체")
data class CreateChallengeRuleResponse(

    @Schema(description = "챌린지 인증 룰 내용")
    val rule: String,
) {

    companion object {

        fun of(challengeRule: ChallengeRule): CreateChallengeRuleResponse {
            return CreateChallengeRuleResponse(challengeRule.rule)
        }
    }
}