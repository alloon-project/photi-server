package com.photi.server.api.controller.challenge.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.server.service.challenge.dto.FindChallengeFeedsDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.time.LocalTime

@Schema(description = "챌린지 피드 조회 응답 객체")
data class FindChallengeFeedsResponse(

    @Schema(description = "피드 id", example = "1")
    val id: Long,

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "피드 인증 날짜")
    val createdDateTime: LocalDateTime,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,

    @Schema(description = "피드 좋아요 여부", example = "true")
    val isLike: Boolean,
) {

    companion object {

        fun of(feed: FindChallengeFeedsDto): FindChallengeFeedsResponse {
            return FindChallengeFeedsResponse(
                feed.id,
                feed.username,
                feed.imageUrl,
                feed.createdDateTime,
                feed.proveTime,
                feed.isLike,
            )
        }

        fun of(feeds: List<FindChallengeFeedsDto>): List<FindChallengeFeedsResponse> =
            feeds.map { of(it) }
    }
}