package com.photi.server.service.challenge.dto

import com.photi.server.domain.challenge.ChallengeRule

data class ChallengeRuleDto(
    val rule: String,
) {

    companion object {

        fun of(challengeRule: ChallengeRule): ChallengeRuleDto {
            return ChallengeRuleDto(challengeRule.rule)
        }

        fun of(rules: List<ChallengeRule>): List<ChallengeRuleDto> {
            return rules.map { of(it) }
        }
    }
}