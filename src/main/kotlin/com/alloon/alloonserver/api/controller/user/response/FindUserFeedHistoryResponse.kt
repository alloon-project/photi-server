package com.alloon.alloonserver.api.controller.user.response

import com.alloon.alloonserver.service.user.dto.FindUserFeedHistoryDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class FindUserFeedHistoryResponse(

    @Schema(description = "피드 id", example = "1")
    val id: Long,

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "피드 인증 날짜", example = "2024-10-23")
    val createdDate: LocalDate,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,
) {

    companion object {

        fun of(feedHistory: FindUserFeedHistoryDto): FindUserFeedHistoryResponse {
            return FindUserFeedHistoryResponse(
                feedHistory.id,
                feedHistory.imageUrl,
                feedHistory.createdDate.toLocalDate(),
                feedHistory.name
            )
        }
    }
}