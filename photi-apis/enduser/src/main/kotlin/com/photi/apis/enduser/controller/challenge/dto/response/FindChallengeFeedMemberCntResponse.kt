package com.photi.apis.enduser.controller.challenge.dto.response

import com.photi.core.domain.challenge.dto.FindChallengeFeedMemberCntDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 피드 당일 인증 파티원 수 조회 응답 객체")
data class FindChallengeFeedMemberCntResponse(

    @Schema(description = "피드 인증한 파티원 수", example = "5")
    val feedMemberCnt: Int,
) {

    companion object {

        fun of(challenge: FindChallengeFeedMemberCntDto) =
            FindChallengeFeedMemberCntResponse(challenge.feedMemberCnt)
    }
}
