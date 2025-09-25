package com.photi.apis.enduser.controller.challenge.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.core.domain.challenge.dto.SearchChallengeByNameDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class SearchChallengeByNameResponse(

    @Schema(description = "챌린지 id", example = "1")
    val id: Long,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "챌린지 파티원 수", example = "5")
    val currentMemberCnt: Int,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    val endDate: LocalDate,

    @Schema(
        description = "챌린지 파티원 이미지 리스트", example = """
        [
            {"memberImage": "https://url.kr/5MhHhD"},
            {"memberImage": "https://url.kr/5MhHhD"},
            {"memberImage": "https://url.kr/5MhHhD"}
        ]
    """
    )
    val memberImages: List<ChallengeMemberImageResponse>,
) {

    companion object {

        fun of(challenge: SearchChallengeByNameDto) = SearchChallengeByNameResponse(
            challenge.id,
            challenge.name,
            challenge.imageUrl,
            challenge.currentMemberCnt,
            challenge.endDate,
            challenge.memberImages.map { ChallengeMemberImageResponse(it) },
        )

        fun of(challenges: List<SearchChallengeByNameDto>) = challenges.map { of(it) }
    }
}
