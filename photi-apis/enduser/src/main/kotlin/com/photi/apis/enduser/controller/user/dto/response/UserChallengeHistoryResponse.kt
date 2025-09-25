package com.photi.apis.enduser.controller.user.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.core.domain.user.dto.UserChallengeHistoryDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

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

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "사용자 회원가입 날짜", example = "2025-06-01")
    val registerDate: LocalDate,
) {

    companion object {

        fun of(challengeHistory: UserChallengeHistoryDto) = UserChallengeHistoryResponse(
            challengeHistory.username,
            challengeHistory.imageUrl,
            challengeHistory.feedCnt,
            challengeHistory.endedChallengeCnt,
            challengeHistory.registerDate.toLocalDate(),
        )
    }
}
