package com.photi.server.api.controller.challenge.response

import com.photi.server.service.challenge.dto.ChallengeMemberImageDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 파티원 이미지 응답 객체")
data class ChallengeMemberImageResponse(

    @Schema(description = "챌린지 파티원 이미지")
    val memberImage: String,
) {

    companion object {

        fun of(challengeMemberImage: ChallengeMemberImageDto): ChallengeMemberImageResponse {
            return ChallengeMemberImageResponse(challengeMemberImage.imageUrl)
        }

        fun of(challengeMemberImages: List<ChallengeMemberImageDto>): List<ChallengeMemberImageResponse> {
            return challengeMemberImages.map { of(it) }
        }
    }
}