package com.photi.apis.enduser.controller.feed.dto.response

import com.photi.core.domain.feed.dto.FindFeedDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "챌린지 피드 개별 조회 응답 객체")
data class FindFeedResponse(

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,

    @Schema(description = "사용자 이미지", example = "https://url.kr/5MhHhD")
    val userImageUrl: String,

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val feedImageUrl: String,

    @Schema(description = "피드 인증 날짜 시간")
    val createdDateTime: LocalDateTime,

    @Schema(description = "피드 하트 수", example = "10")
    val likeCnt: Int,

    @Schema(description = "피드 좋아요 여부", example = "true")
    val isLike: Boolean,
) {

    companion object {

        fun of(feed: FindFeedDto) = FindFeedResponse(
            feed.username,
            feed.userImageUrl,
            feed.feedImageUrl,
            feed.createdDateTime,
            feed.likeCount,
            feed.isLike,
        )
    }
}
