package com.photi.apis.enduser.controller.user.dto.response

import com.photi.core.domain.user.dto.FindFeedHistoryDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "사용자 피드 인증 횟수 모아보기 조회 응답 객체")
data class FindFeedHistoryResponse(

    @Schema(description = "피드 id", example = "1")
    val feedId: Long,

    @Schema(description = "챌린지 id", example = "1")
    val challengeId: Long,

    @Schema(description = "피드 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "피드 인증 날짜", example = "2024-10-23")
    val createdDate: LocalDate,

    @Schema(description = "챌린지 이름", example = "신나게 하는 러닝 챌린지")
    val name: String,

    @Schema(description = "챌린지 초대코드", example = "478DS")
    val invitationCode: String,

    @Schema(description = "탈퇴한 챌린지 여부", example = "true")
    val isDeleted: Boolean,
) {

    companion object {

        fun of(feedHistory: FindFeedHistoryDto) = FindFeedHistoryResponse(
            feedHistory.feedId,
            feedHistory.challengeId,
            feedHistory.imageUrl,
            feedHistory.createdDate.toLocalDate(),
            feedHistory.name,
            feedHistory.invitationCode,
            feedHistory.status.isDeleted(),
        )
    }
}
