package com.alloon.alloonserver.service.challenge.dto

import java.time.LocalDate
import java.time.LocalTime

data class UpdateChallengeInfoDto(
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
)