package com.photi.apis.enduser.controller.user.dto.response

import com.photi.core.domain.user.dto.FindChallengeCountDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 참여 중인 챌린지 갯수 응답 객체")
data class FindChallengeCountResponse(

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,

    @Schema(description = "사용자 참여 중인 챌린지 갯수", example = "5")
    val challengeCnt: Int,
) {

    companion object {

        fun of(challengeCount: FindChallengeCountDto) =
            FindChallengeCountResponse(challengeCount.username, challengeCount.challengeCount)
    }
}
