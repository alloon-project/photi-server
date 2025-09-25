package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.ChallengeRule

data class ChallengeRuleDto(
    val rule: String,
) {

    companion object {

        fun of(challengeRule: ChallengeRule) = ChallengeRuleDto(challengeRule.rule)

        fun of(rules: List<ChallengeRule>) = rules.map { of(it) }
    }
}
