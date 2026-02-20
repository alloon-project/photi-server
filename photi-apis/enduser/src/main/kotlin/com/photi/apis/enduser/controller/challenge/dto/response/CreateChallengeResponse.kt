package com.photi.apis.enduser.controller.challenge.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.core.domain.challenge.dto.CreateChallengeDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "챌린지 개최 응답 객체")
data class CreateChallengeResponse(

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
    val rules: List<ChallengeRuleResponse>,

    @Schema(
        description = "챌린지 해시태그 리스트", example = """
        [
            {"hashtag": "러닝"},
            {"hashtag": "건강"}
        ]
    """
    )
    val hashtags: List<ChallengeHashtagResponse>,
) {

    companion object {

        fun of(challenge: CreateChallengeDto) = CreateChallengeResponse(
            challenge.id,
            challenge.name,
            challenge.goal,
            challenge.proveTime,
            challenge.endDate,
            challenge.imageUrl,
            ChallengeRuleResponse.of(challenge.rules),
            ChallengeHashtagResponse.of(challenge.hashtags),
        )
    }
}
