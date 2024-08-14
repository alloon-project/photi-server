package com.alloon.alloonserver.service.challenge.dto

import java.time.LocalDate
import java.time.LocalTime

data class CreateChallengeDto(
    val name: String,
    val isPublic: Boolean,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val imageUrl: String,
    val rules: List<CreateChallengeRuleDto>,
    val hashtags: List<CreateChallengeHashtagDto>,
)