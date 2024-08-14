package com.alloon.alloonserver.service.mission.dto

import com.alloon.alloonserver.domain.mission.Mission
import java.time.LocalDate
import java.time.LocalTime

data class FindMissionInfoDto(
    val rules: List<CreateMissionRuleDto>,
    val proveTime: LocalTime,
    val goal: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {

    companion object {

        fun of(mission: Mission): FindMissionInfoDto {
            return FindMissionInfoDto(
                CreateMissionRuleDto.of(mission.rules),
                mission.proveTime,
                mission.goal,
                mission.startDate,
                mission.endDate
            )
        }
    }
}