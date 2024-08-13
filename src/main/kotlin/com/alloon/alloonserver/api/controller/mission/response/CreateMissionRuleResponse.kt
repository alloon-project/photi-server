package com.alloon.alloonserver.api.controller.mission.response

import com.alloon.alloonserver.domain.mission.MissionRule
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 인증 룰 응답 객체")
data class CreateMissionRuleResponse(

    @Schema(description = "챌린지 인증 룰 내용")
    val rule: String,
) {

    companion object {

        fun of(missionRule: MissionRule): CreateMissionRuleResponse {
            return CreateMissionRuleResponse(missionRule.rule)
        }
    }
}