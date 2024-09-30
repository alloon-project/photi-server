package com.alloon.alloonserver.api.controller.user.response

import com.alloon.alloonserver.service.user.dto.UserChallengeHistoryDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 챌린지 기록 응답 객체")
data class UserChallengeHistoryResponse(

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,

    @Schema(description = "사용자 프로필 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "사용자 챌린지 인증 횟수", example = "99")
    val feedCnt: Int,

    @Schema(description = "사용자 종료된 챌린지 갯수", example = "2")
    val endedChallengeCnt: Int,
) {

    companion object {

        fun of(challengeHistory: UserChallengeHistoryDto): UserChallengeHistoryResponse {
            return UserChallengeHistoryResponse(
                challengeHistory.username,
                challengeHistory.imageUrl,
                challengeHistory.feedCnt,
                challengeHistory.endedChallengeCnt,
            )
        }
    }
}