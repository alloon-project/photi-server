package com.photi.server.api.controller.challenge.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.server.service.challenge.dto.FindChallengeInfoDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "챌린지 소개 조회 응답 객체")
data class FindChallengeInfoResponse(

    @Schema(
        description = "챌린지 인증 룰 리스트", example = """
        [
            {"rule": "장소 나오게 찍기"},
            {"rule": "일주일에 3회 이상 인증하기"},
            {"rule": "얼굴 안 나오게 찍기"}
        ]
    """
    )
    val rules: List<ChallengeRuleResponse>,

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

        fun of(challengeInfo: FindChallengeInfoDto): FindChallengeInfoResponse {
            return FindChallengeInfoResponse(
                ChallengeRuleResponse.of(challengeInfo.rules),
                challengeInfo.proveTime,
                challengeInfo.goal,
                challengeInfo.startDate,
                challengeInfo.endDate
            )
        }
    }
}