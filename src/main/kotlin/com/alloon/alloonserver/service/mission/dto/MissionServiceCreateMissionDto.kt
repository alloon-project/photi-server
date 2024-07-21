package com.alloon.alloonserver.service.mission.dto

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateHashTagRequest
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateMissionRuleRequest
import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.domain.user.User
import java.time.LocalDate

data class MissionServiceCreateMissionDto(
    val missionName: String,
    val missionDescription: String,
    val missionGoal: String,
    val missionRules: List<MissionCreateMissionRuleRequest>,
    val missionImageUrl: String,
    val missionEndDate: LocalDate,
    val hashtags: List<MissionCreateHashTagRequest>,
) {

    fun toMissionMember(user: User): MissionMember {
        return MissionMember(
            user = user,
            mission = Mission(
                missionName = missionName,
                description = missionDescription,
                goal = missionGoal,
                imageUrl = missionImageUrl,
                endDate = missionEndDate,
                hashtags = this.hashtags.map{ it.hashtag }
            ),
            creatorYn = true
        )
    }

    fun toMissionRule(mission: Mission): List<MissionRule> {
        return missionRules.map { MissionRule(mission = mission, rule = it.missionRule) }
    }
}
