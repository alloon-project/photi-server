package com.photi.apis.enduser.controller.feed.dto.response

import com.photi.core.domain.feed.dto.RegisterFeedDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "피드 인증 응답 객체")
data class RegisterFeedResponse(

    @Schema(description = "피드 id", example = "1")
    val id: Long,

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "피드 인증 날짜")
    val createdDateTime: LocalDateTime,

    @Schema(description = "피드 좋아요 여부", example = "true")
    val isLike: Boolean,
) {

    companion object {

        fun of(feed: RegisterFeedDto) = RegisterFeedResponse(
            feed.id,
            feed.imageUrl,
            feed.createdDateTime,
            feed.isLike,
        )
    }
}
