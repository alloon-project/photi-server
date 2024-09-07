package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.FindChallengesDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "챌린지 응답 객체")
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
    val hashtags: List<CreateChallengeHashtagResponse>,
) {

    companion object {

        fun of(challenge: FindChallengesDto): FindChallengesResponse {
            return FindChallengesResponse(
                challenge.id,
                challenge.name,
                challenge.imageUrl,
                challenge.endDate,
                challenge.hashtags.map { it ->
                    CreateChallengeHashtagResponse(it)
                }
            )
        }

        fun of(challenges: List<FindChallengesDto>): List<FindChallengesResponse> =
            challenges.map { of(it) }
    }
}