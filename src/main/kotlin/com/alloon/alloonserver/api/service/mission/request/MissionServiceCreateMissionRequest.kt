package com.alloon.alloonserver.api.service.mission.request

import com.alloon.alloonserver.domain.mission.Hashtag
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.MissionHashtag
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.user.User
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class MissionServiceCreateMissionRequest(
    @field:Size(min = 2, max = 30, message = "미션명은 2~30자만 가능합니다.")
    var missionName: String,
    @field:Size(min = 1, max = 500, message = "미션 소개는 1~500자만 가능합니다.")
    var missionDescription: String,
    @field:Size(max = 500, message = "규칙은 0~500자만 가능합니다.")
    var missionRule: String?,
    @field:Size(max = 500, message = "목표는 0~500자만 가능합니다.")
    var missionGoal: String?,
    @field:Size(max = 500, message = "미션 대표 이미지는 0~500자만 가능합니다.")
    var missionImageUrl: String?,

    var missionEndedDate: LocalDate,

    @field:Size(max = 5, message = "해시태그는 0~5개만 가능합니다.")
    @field:Valid
    var hashtags: List<MissionCreateHashTagServiceRequest>
) {
    fun toMissionMember(user: User): MissionMember {
        return MissionMember(
            user = user,
            mission = Mission(
                missionName = missionName,
                description = missionDescription,
                rule = missionRule,
                goal = missionGoal,
                imageUrl = missionImageUrl,
                endedDate = missionEndedDate
            ),
            isCreator = true
        )
    }

    fun toMissionHashtag(mission: Mission): List<MissionHashtag> {
        return hashtags?.map { MissionHashtag(mission = mission, hashtag = Hashtag(hashtag = it.hashtag)) }
            ?: emptyList()
    }

}

data class MissionCreateHashTagServiceRequest(
    @field:Size(min = 1, max = 30, message = "해시태그는 1~30자만 가능합니다.")
    var hashtag: String,
)
