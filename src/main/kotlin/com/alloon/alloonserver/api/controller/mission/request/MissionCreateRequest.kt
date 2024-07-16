package com.alloon.alloonserver.api.controller.mission.request

import com.alloon.alloonserver.service.mission.dto.MissionServiceCreateMissionDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "챌린지 생성 요청 객체")
data class MissionCreateRequest(

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    @field:NotBlank(message = "미션명은 필수 입력입니다.")
    @field:Size(min = 2, max = 16, message = "미션명은 2~16자만 가능합니다.")
    val missionName: String,

    @Schema(description = "챌린지 소개", example = "신나게 하는 러닝 챌린지 소개입니다.")
    @field:NotBlank(message = "미션 소개는 필수 입력입니다.")
    @field:Size(min = 10, max = 120, message = "미션 소개는 10~120자만 가능합니다.")
    val missionDescription: String,

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    @field:NotBlank(message = "목표는 필수 입력입니다.")
    @field:Size(min = 1, max = 30, message = "목표는 1~30자만 가능합니다.")
    val missionGoal: String,

    @Schema(description = "챌린지 인증 룰 리스트", example = "[\"장소 나오게 찍기\", \"일주일에 3회 이상 인증하기\", \"얼굴 안 나오게 찍기\"]")
    @field:Size(max = 5, message = "규칙은 0~5개만 가능합니다.")
    @field:Valid
    val missionRules: List<MissionCreateMissionRuleRequest>,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    @field:NotBlank(message = "미션 대표 이미지는 필수 입력입니다.")
    @field:Size(min = 1, max = 500, message = "미션 대표 이미지는 1~500자만 가능합니다.")
    val missionImageUrl: String,

    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    @field:NotNull(message = "미션 종료일은 필수 입력입니다.")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val missionEndDate: LocalDate,

    @Schema(description = "챌린지 해시태그 리스트", example = "[\"러닝\", \"건강\"]")
    @field:NotNull(message = "해시태그는 필수 입력입니다.")
    @field:Size(min = 1, max = 5, message = "해시태그는 1~5개만 가능합니다.")
    @field:Valid
    val hashtags: List<MissionCreateHashTagRequest>,
) {

    fun toServiceDto(): MissionServiceCreateMissionDto {
        return MissionServiceCreateMissionDto(
            missionName,
            missionDescription,
            missionGoal,
            missionRules,
            missionImageUrl,
            missionEndDate,
            hashtags
        )
    }
}

@Schema(description = "챌린지 인증 룰")
data class MissionCreateMissionRuleRequest(
    @field:Size(min = 1, max = 30, message = "규칙은 1~30자만 가능합니다.")
    val missionRule: String,
)

@Schema(description = "챌린지 해시태그")
data class MissionCreateHashTagRequest(
    @field:Size(min = 1, max = 5, message = "해시태그는 1~5자만 가능합니다.")
    val hashtag: String,
)
