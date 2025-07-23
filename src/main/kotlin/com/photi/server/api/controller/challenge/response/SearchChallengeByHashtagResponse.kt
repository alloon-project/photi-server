package com.photi.server.api.controller.challenge.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.server.service.challenge.dto.SearchChallengeByHashtagDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class SearchChallengeByHashtagResponse(

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
        description = "챌린지 해시태그 리스트", example = """
        [
            {"hashtag": "러닝"},
            {"hashtag": "건강"}
        ]
    """
    )
    val hashtags: List<ChallengeHashtagResponse>,

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

        fun of(challenge: SearchChallengeByHashtagDto): SearchChallengeByHashtagResponse {
            return SearchChallengeByHashtagResponse(
                challenge.id,
                challenge.name,
                challenge.imageUrl,
                challenge.currentMemberCnt,
                challenge.endDate,
                challenge.hashtags.map { ChallengeHashtagResponse(it.hashtag) },
                challenge.memberImages.map { ChallengeMemberImageResponse(it) },
            )
        }

        fun of(challenges: List<SearchChallengeByHashtagDto>): List<SearchChallengeByHashtagResponse> {
            return challenges.map { of(it) }
        }
    }
}
