package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.ChallengeRule

data class ChallengeRuleDto(
    val rule: String,
) {

    fun toEntity(challenge: Challenge) = ChallengeRule(challenge = challenge, rule = rule)

    companion object {

        fun of(challengeRule: ChallengeRule) = ChallengeRuleDto(challengeRule.rule)

        fun of(rules: List<ChallengeRule>) = rules.map { of(it) }
    }
}
