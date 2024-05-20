package com.alloon.alloonserver.api.service.mission.response

import com.alloon.alloonserver.domain.mission.Hashtag
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.MissionRule
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class MissionCreateResponse(
    val missionId: Long,
    val missionName: String,
    val description: String,
    val goal: String?,
    val rules: List<MissionCreateRuleResponse>,
    var imageUrl: String,
    var currentMemberCnt: Int,
    var missionCreator: MissionCreateCreatorResponse,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val startDate: LocalDate,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate,
    val hashtags: List<MissionCreateHashtagResponse>,
) {

    constructor(creator: MissionMember, rules: List<MissionRule>, hashtags: List<Hashtag>): this(
        creator.mission.id!!,
        creator.mission.missionName,
        creator.mission.description,
        creator.mission.goal,
        rules.map { rule -> MissionCreateRuleResponse(rule) }.toList(),
        creator.mission.imageUrl,
        creator.mission.currentMemberCnt,
        MissionCreateCreatorResponse(creator),
        creator.mission.startDate,
        creator.mission.endDate,
        hashtags.map { hashtag -> MissionCreateHashtagResponse(hashtag) }.toList()
    )
}

data class MissionCreateCreatorResponse(
    val username: String,
    val imageUrl: String?,
) {

    constructor(creator: MissionMember) : this(creator.user!!.username, creator.user.imageUrl)
}

data class MissionCreateRuleResponse(
    val missionRuleId: Long,
    val rule: String,
) {

    constructor(missionRule: MissionRule) : this(missionRule.id!!, missionRule.rule)
}

data class MissionCreateHashtagResponse(
    val hashtagId: Long,
    val tag: String,
) {

    constructor(hashtag: Hashtag) : this(hashtag.id!!, hashtag.tag)
}