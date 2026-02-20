package com.photi.core.domain.challenge.dto

import java.time.LocalDate
import java.time.LocalTime

data class UpdateChallengeDto(
    val name: String,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val imageUrl: String,
    val rules: List<ChallengeRuleDto>,
    val hashtags: List<ChallengeHashtagDto>,
)
