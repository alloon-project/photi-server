package com.alloon.alloonserver.api.service.mission.request

import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "챌린지 생성 요청 객체")
data class MissionServiceCreateMissionRequest(
    @field:Size(min = 2, max = 16, message = "미션명은 2~16자만 가능합니다.")
    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    var missionName: String,
    @field:Size(min = 10, max = 120, message = "미션 소개는 10~120자만 가능합니다.")
    @Schema(description = "챌린지 소개", example = "신나게 하는 러닝 챌린지 소개입니다.")
    var missionDescription: String,
    @field:Size(min = 1, max = 30, message = "목표는 1~30자만 가능합니다.")
    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    var missionGoal: String,

    @field:Size(max = 5, message = "규칙은 0~5개만 가능합니다.")
    @field:Valid
    @Schema(
        description = "챌린지 인증 룰 리스트",
        example = "[\"장소 나오게 찍기\", \"일주일에 3회 이상 인증하기\", \"얼굴 안 나오게 찍기\"]",
        implementation = MissionCreateMissionRuleServiceRequest::class,
    )
    var missionRules: List<MissionCreateMissionRuleServiceRequest>,

    @field:Size(min = 1, max = 500, message = "미션 대표 이미지는 1~500자만 가능합니다.")
    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    var missionImageUrl: String,

    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    var missionEndDate: LocalDate,

    @field:Size(min = 1, max = 5, message = "해시태그는 1~5개만 가능합니다.")
    @field:Valid
    @Schema(
        description = "챌린지 해시태그 리스트",
        example = "[\"러닝\", \"건강\"]",
        implementation = MissionCreateHashTagServiceRequest::class
    )
    var hashtags: List<MissionCreateHashTagServiceRequest>,
) {
    fun toMissionMember(user: User): MissionMember {
        return MissionMember(
            user = user,
            mission = Mission(
                missionName = missionName,
                description = missionDescription,
                goal = missionGoal,
                imageUrl = missionImageUrl,
                endDate = missionEndDate
            ),
            creatorYn = true
        )
    }

    fun toMissionRule(mission: Mission): List<MissionRule> {
        return missionRules.map { MissionRule(mission = mission, rule = it.missionRule) }
    }

    fun toMissionHashtag(mission: Mission): List<MissionHashtag> {
        return hashtags.map { MissionHashtag(mission = mission, hashtag = Hashtag(tag = it.hashtag)) }
    }
}

data class MissionCreateMissionRuleServiceRequest(
    @field:Size(min = 1, max = 30, message = "규칙은 1~30자만 가능합니다.")
    @Schema(description = "챌린지 인증 룰")
    var missionRule: String,
)

data class MissionCreateHashTagServiceRequest(
    @field:Size(min = 1, max = 5, message = "해시태그는 1~5자만 가능합니다.")
    @Schema(description = "챌린지 해시태그")
    var hashtag: String,
)
