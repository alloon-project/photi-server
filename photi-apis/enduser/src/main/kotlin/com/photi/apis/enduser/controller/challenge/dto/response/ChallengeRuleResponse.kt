package com.photi.apis.enduser.controller.challenge.dto.response

import com.photi.core.domain.challenge.dto.ChallengeRuleDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 인증 룰 응답 객체")
data class ChallengeRuleResponse(

    @Schema(description = "챌린지 인증 룰 내용")
    val rule: String,
) {

    companion object {

        fun of(challengeRule: ChallengeRuleDto) = ChallengeRuleResponse(challengeRule.rule)

        fun of(challengeRules: List<ChallengeRuleDto>) = challengeRules.map { of(it) }
    }
}
