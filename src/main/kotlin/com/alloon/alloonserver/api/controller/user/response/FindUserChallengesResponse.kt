package com.alloon.alloonserver.api.controller.user.response

import com.alloon.alloonserver.api.controller.challenge.response.ChallengeHashtagResponse
import com.alloon.alloonserver.service.user.dto.FindUserChallengesDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "사용자 참여 중인 챌린지 조회 응답 객체")
data class FindUserChallengesResponse(

    @Schema(description = "챌린지 id", example = "1")
    val id: Long,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val challengeImageUrl: String,

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

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val feedImageUrl: String,
) {

    companion object {

        fun of(challenge: FindUserChallengesDto): FindUserChallengesResponse {
            return FindUserChallengesResponse(
                challenge.id,
                challenge.name,
                challenge.challengeImageUrl,
                challenge.proveTime,
                challenge.endDate,
                challenge.hashtags.map { ChallengeHashtagResponse(it.hashtag) },
                challenge.feedImageUrl,
            )
        }
    }
}