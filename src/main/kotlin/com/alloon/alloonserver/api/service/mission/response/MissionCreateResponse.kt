package com.alloon.alloonserver.api.service.mission.response

import com.alloon.alloonserver.domain.mission.Hashtag
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.MissionRule
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class MissionCreateResponse(
    val missionId: Long,
    val missionName: String,
    val missionDescription: String,
    val missionGoal: String?,
    val missionRules: List<String>,
    var missionImageUrl: String,
    var currentMemberCnt: Int,
    var missionCreator: MissionCreateCreatorResponse,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val missionStartDate: LocalDate,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val missionEndDate: LocalDate,
    val hashtags: List<String>,
) {

    constructor(creator: MissionMember, rules: List<String>, hashtags: List<Hashtag>): this(
        creator.mission.id!!,
        creator.mission.missionName,
        creator.mission.description,
        creator.mission.goal,
        rules,
        creator.mission.imageUrl,
        creator.mission.currentMemberCnt,
        MissionCreateCreatorResponse(creator),
        creator.mission.startDate,
        creator.mission.endDate,
        hashtags.map { it.hashtag }
    )
}

data class MissionCreateCreatorResponse(
    val username: String,
    val imageUrl: String?,
) {

    constructor(creator: MissionMember) : this(creator.user!!.username, creator.user.imageUrl)
}