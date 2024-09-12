package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.FindChallengeDto
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

@Schema(description = "챌린지 개별 조회 응답 객체")
data class FindChallengeResponse(

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 목표", example = "하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.")
    val goal: String,

    @Schema(description = "챌린지 대표 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "챌린지 파티원 수", example = "5")
    val currentMemberCnt: Int,

    @Schema(description = "챌린지 공개 여부", example = "true")
    val isPublic: Boolean,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

    @Schema(description = "챌린지 종료 날짜", example = "2024-12-01")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate,

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

        fun of(challenge: FindChallengeDto): FindChallengeResponse {
            return FindChallengeResponse(
                challenge.name,
                challenge.goal,
                challenge.imageUrl,
                challenge.currentMemberCnt,
                challenge.isPublic,
                challenge.proveTime,
                challenge.endDate,
                ChallengeRuleResponse.of(challenge.rules),
                ChallengeHashtagResponse.of(challenge.hashtags),
                ChallengeMemberImageResponse.of(challenge.memberImages),
            )
        }
    }
}