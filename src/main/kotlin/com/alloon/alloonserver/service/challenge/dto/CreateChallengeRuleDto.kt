package com.alloon.alloonserver.service.challenge.dto

import com.alloon.alloonserver.domain.challenge.ChallengeRule

data class CreateChallengeRuleDto(
    val rule: String,
) {

    companion object {

        fun of(challengeRule: ChallengeRule): CreateChallengeRuleDto {
            return CreateChallengeRuleDto(challengeRule.rule)
        }

        fun of(rules: List<ChallengeRule>): List<CreateChallengeRuleDto> {
            return rules.map { of(it) }
        }
    }
}