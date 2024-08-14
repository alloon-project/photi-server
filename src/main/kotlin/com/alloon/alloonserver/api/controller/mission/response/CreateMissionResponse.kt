package com.alloon.alloonserver.api.controller.mission.response

import com.alloon.alloonserver.domain.mission.Mission
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "챌린지 생성 응답 객체")
data class CreateMissionResponse(

    @Schema(description = "챌린지 id", example = "1")
    val id: Long?,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    val goal: String,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    val endDate: LocalDate,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(
        description = "챌린지 인증 룰 리스트", example = """
        [
            {"rule": "장소 나오게 찍기"},
            {"rule": "일주일에 3회 이상 인증하기"},
            {"rule": "얼굴 안 나오게 찍기"}
        ]
    """
    )
    val rules: List<CreateMissionRuleResponse>,

    @Schema(
        description = "챌린지 해시태그 리스트", example = """
        [
            {"hashtag": "러닝"},
            {"hashtag": "건강"}
        ]
    """
    )
    val hashtags: List<CreateMissionHashtagResponse>,
) {

    companion object {

        fun of(mission: Mission): CreateMissionResponse {
            return CreateMissionResponse(
                mission.id,
                mission.name,
                mission.goal,
                mission.proveTime,
                mission.endDate,
                mission.imageUrl,
                mission.rules.map {
                    CreateMissionRuleResponse.of(it)
                },
                mission.hashtags.map {
                    CreateMissionHashtagResponse.of(it)
                }
            )
        }
    }
}