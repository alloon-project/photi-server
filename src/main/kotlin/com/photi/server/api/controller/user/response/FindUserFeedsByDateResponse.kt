package com.photi.server.api.controller.user.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.server.service.user.dto.FindUserFeedsByDateDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

@Schema(description = "사용자 피드 인증 개별 날짜 조회 응답 객체")
data class FindUserFeedsByDateResponse(

    @Schema(description = "피드 id", example = "1")
    val feedId: Long,

    @Schema(description = "챌린지 id", example = "1")
    val challengeId: Long,

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 인증 시간", example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "kk:mm")
    val proveTime: LocalTime,
) {

    companion object {

        fun of(feed: FindUserFeedsByDateDto): FindUserFeedsByDateResponse {
            return FindUserFeedsByDateResponse(
                feed.feedId,
                feed.challengeId,
                feed.imageUrl,
                feed.name,
                feed.proveTime,
            )
        }

        fun of(feeds: List<FindUserFeedsByDateDto>): List<FindUserFeedsByDateResponse> {
            return feeds.map { of(it) }
        }
    }
}