package com.alloon.alloonserver.api.controller.mission.request

import com.alloon.alloonserver.api.service.mission.request.MissionCreateHashTagServiceRequest
import com.alloon.alloonserver.api.service.mission.request.MissionServiceCreateMissionRequest
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class MissionCreateRequest(
    @field:NotBlank(message = "미션명은 필수 입력입니다.")
    var missionName: String,
    @field:NotBlank(message = "미션 소개는 필수 입력입니다.")
    var missionDescription: String,

    var missionRule: String?,
    var missionGoal: String?,
    var missionImageUrl: String?,

    @field:NotNull(message = "미션 종료일은 필수 입력입니다.")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var missionEndDate: LocalDate?,

    var hashtags:List<String> = listOf()
) {

    fun toServiceRequest(): MissionServiceCreateMissionRequest {
        return MissionServiceCreateMissionRequest(
            missionName,
            missionDescription,
            missionRule,
            missionGoal,
            missionImageUrl,
            missionEndDate!!,
            hashtags.map { MissionCreateHashTagServiceRequest(it) }
        )
    }
}
