package com.alloon.alloonserver.service.mission.response

import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.MissionRule
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "챌린지 생성 응답 객체")
data class MissionCreateResponse(
    @Schema(description = "챌린지 id", example = "1")
    val missionId: Long,
    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val missionName: String,
    @Schema(description = "챌린지 소개", example = "신나게 하는 러닝 챌린지 소개입니다.")
    val description: String,
    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    val goal: String?,
    @Schema(
        description = "챌린지 인증 룰 리스트",
        example = "[{\"missionRuleId\": 1,\n \"rule\": \"장소 나오게 찍기\"}, {\"missionRuleId\": 2,\n \"rule\": \"일주일에 3회 이상 인증하기\"}, {\"missionRuleId\": 3,\n \"rule\": \"얼굴 안 나오게 찍기\"}]",
        implementation = MissionCreateRuleResponse::class,
    )
    val rules: List<MissionCreateRuleResponse>,
    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    var imageUrl: String,
    @Schema(description = "챌린지 현재 파티원 수", example = "1")
    var currentMemberCnt: Int,
    @Schema(
        description = "챌린지 파티장",
        example = "{\"username\": \"photi\",\n \"imageUrl\": \"https://url.kr/5MhHhD\"}",
        implementation = MissionCreateCreatorResponse::class
    )
    var missionCreator: MissionCreateCreatorResponse,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 시작 날짜", example = "2024-06-27")
    val startDate: LocalDate,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    val endDate: LocalDate,

    val hashtags : List<String> = listOf()
) {

    constructor(creator: MissionMember, rules: List<MissionRule>) : this(
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
        creator.mission.hashtags
    )
}

data class MissionCreateCreatorResponse(
    @Schema(description = "파티장 아이디")
    val username: String,
    @Schema(description = "파티장 프로필 이미지 url")
    val imageUrl: String?,
) {

    constructor(creator: MissionMember) : this(creator.user!!.username, creator.user.imageUrl)
}

data class MissionCreateRuleResponse(
    @Schema(description = "챌린지 인증 룰 id")
    val missionRuleId: Long,
    @Schema(description = "챌린지 인증 룰 내용")
    val rule: String,
) {

    constructor(missionRule: MissionRule) : this(missionRule.id!!, missionRule.rule)
}