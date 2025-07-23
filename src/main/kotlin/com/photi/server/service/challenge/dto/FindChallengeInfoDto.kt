package com.photi.server.service.challenge.dto

import com.photi.server.domain.challenge.Challenge
import java.time.LocalDate
import java.time.LocalTime

data class FindChallengeInfoDto(
    val rules: List<ChallengeRuleDto>,
    val proveTime: LocalTime,
    val goal: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {

    companion object {

        fun of(challenge: Challenge): FindChallengeInfoDto {
            return FindChallengeInfoDto(
                ChallengeRuleDto.of(challenge.rules),
                challenge.proveTime,
                challenge.goal,
                challenge.startDate,
                challenge.endDate
            )
        }
    }
}