package com.alloon.alloonserver.api.controller.mission.response

import com.alloon.alloonserver.service.mission.dto.FindMissionInfoDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

data class FindMissionInfoResponse(

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

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    val goal: String,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 시작 날짜", example = "2024-01-01")
    val startDate: LocalDate,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    val endDate: LocalDate,
) {

    companion object {

        fun of(missionInfo: FindMissionInfoDto): FindMissionInfoResponse {
            return FindMissionInfoResponse(
                missionInfo.rules.map {
                    CreateMissionRuleResponse(it.rule)
                },
                missionInfo.proveTime,
                missionInfo.goal,
                missionInfo.startDate,
                missionInfo.endDate
            )
        }
    }
}