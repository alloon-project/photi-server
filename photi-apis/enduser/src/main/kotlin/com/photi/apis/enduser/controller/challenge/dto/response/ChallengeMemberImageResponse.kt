package com.photi.apis.enduser.controller.challenge.dto.response

import com.photi.core.domain.challenge.dto.ChallengeMemberImageDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챌린지 파티원 이미지 응답 객체")
data class ChallengeMemberImageResponse(

    @Schema(description = "챌린지 파티원 이미지")
    val memberImage: String,
) {

    companion object {

        fun of(challengeMemberImage: ChallengeMemberImageDto) =
            ChallengeMemberImageResponse(challengeMemberImage.imageUrl)

        fun of(challengeMemberImages: List<ChallengeMemberImageDto>) =
            challengeMemberImages.map { of(it) }
    }
}
