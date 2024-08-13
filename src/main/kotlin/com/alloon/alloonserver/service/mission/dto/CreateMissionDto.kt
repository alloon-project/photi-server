package com.alloon.alloonserver.service.mission.dto

import java.time.LocalDate
import java.time.LocalTime

data class CreateMissionDto(
    val name: String,
    val isPublic: Boolean,
    val goal: String,
    val proveTime: LocalTime,
    val endDate: LocalDate,
    val imageUrl: String,
    val rules: List<CreateMissionRuleDto>,
    val hashtags: List<CreateMissionHashtagDto>,
)