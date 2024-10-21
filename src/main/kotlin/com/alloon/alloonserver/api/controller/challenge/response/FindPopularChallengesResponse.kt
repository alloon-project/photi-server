package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.FindPopularChallengesDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

data class FindPopularChallengesResponse(

    @Schema(description = "챌린지 id", example = "1")
    val id: Long,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    val goal: String,

    @Schema(description = "챌린지 파티원 수", example = "5")
    val currentMemberCnt: Int,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

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

        fun of(challenge: FindPopularChallengesDto): FindPopularChallengesResponse {
            return FindPopularChallengesResponse(
                challenge.id,
                challenge.name,
                challenge.imageUrl,
                challenge.goal,
                challenge.currentMemberCnt,
                challenge.proveTime,
                challenge.endDate,
                challenge.hashtags.map { ChallengeHashtagResponse(it) },
                challenge.memberImages.map { ChallengeMemberImageResponse(it) },
            )
        }

        fun of(challenges: List<FindPopularChallengesDto>): List<FindPopularChallengesResponse> {
            return challenges.map { of(it) }
        }
    }
}