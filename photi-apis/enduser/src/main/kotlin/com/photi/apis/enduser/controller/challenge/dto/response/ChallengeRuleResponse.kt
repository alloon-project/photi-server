package com.photi.apis.enduser.controller.challenge.dto.response

import com.photi.core.domain.challenge.dto.ChallengeRuleDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 인증 룰 응답 객체")
data class ChallengeRuleResponse(

    @Schema(description = "챌린지 인증 룰 내용")
    val rule: String,
) {

    companion object {

        @JvmName("ofFromDto")
        fun of(rules: List<ChallengeRuleDto>) = rules.map { of(it) }

        @JvmName("ofFromString")
        fun of(rules: List<String>) = rules.map { of(it) }

        private fun of(rule: ChallengeRuleDto) = ChallengeRuleResponse(rule.rule)

        private fun of(rule: String) = ChallengeRuleResponse(rule)
    }
}
