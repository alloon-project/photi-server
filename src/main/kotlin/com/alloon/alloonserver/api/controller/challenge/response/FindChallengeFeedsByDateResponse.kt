package com.alloon.alloonserver.api.controller.challenge.response

import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedsDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "챌린지 피드 인증 날짜별 응답 객체")
data class FindChallengeFeedsByDateResponse(

    @Schema(description = "피드 인증 날짜", example = "2024-10-09")
    val createdDate: LocalDate,

    @Schema(description = "피드 목록")
    val feeds: List<FindChallengeFeedsResponse>
) {

    companion object {

        fun of(
            createdDate: LocalDate,
            feeds: List<FindChallengeFeedsDto>
        ): FindChallengeFeedsByDateResponse {
            return FindChallengeFeedsByDateResponse(
                createdDate,
                FindChallengeFeedsResponse.of(feeds)
            )
        }
    }
}