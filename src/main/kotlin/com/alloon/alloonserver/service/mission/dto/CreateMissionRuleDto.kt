package com.alloon.alloonserver.service.mission.dto

import com.alloon.alloonserver.domain.mission.MissionRule

data class CreateMissionRuleDto(
    val rule: String,
) {

    companion object {

        fun of(missionRule: MissionRule): CreateMissionRuleDto {
            return CreateMissionRuleDto(missionRule.rule)
        }

        fun of(rules: List<MissionRule>): List<CreateMissionRuleDto> {
            return rules.map { of(it) }
        }
    }
}