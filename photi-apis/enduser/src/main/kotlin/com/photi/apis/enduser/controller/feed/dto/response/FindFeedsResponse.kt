package com.photi.apis.enduser.controller.feed.dto.response

import com.photi.core.domain.feed.dto.FeedDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "챌린지 피드 인증 날짜별 응답 객체")
data class FindFeedsResponse(

    @Schema(description = "피드 인증 날짜", example = "2024-10-09")
    val createdDate: LocalDate,

    @Schema(description = "피드 인증한 파티원 수", example = "5")
    val feedMemberCnt: Int,

    @Schema(description = "피드 목록")
    val feeds: List<FindFeedsV2Response>
) {

    companion object {

        fun of(dto: Triple<LocalDate, List<FeedDto>, Int>): FindFeedsResponse {
            val (createdDate, feeds, feedMemberCnt) = dto
            return FindFeedsResponse(
                createdDate,
                feedMemberCnt,
                FindFeedsV2Response.of(feeds),
            )
        }
    }
}
