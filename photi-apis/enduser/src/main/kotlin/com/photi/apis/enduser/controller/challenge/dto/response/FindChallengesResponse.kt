package com.photi.apis.enduser.controller.challenge.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.core.domain.challenge.dto.FindChallengesDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "챌린지 조회 응답 객체")
data class FindChallengesResponse(

    @Schema(description = "챌린지 id", example = "1")
    val id: Long,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    val endDate: LocalDate,

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

        fun of(challenge: FindChallengesDto) = FindChallengesResponse(
            challenge.id,
            challenge.name,
            challenge.imageUrl,
            challenge.endDate,
            challenge.hashtags.map {
                ChallengeHashtagResponse(it.hashtag)
            }
        )

        fun of(challenges: List<FindChallengesDto>) = challenges.map { of(it) }
    }
}
