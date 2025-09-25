package com.photi.apis.enduser.controller.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 챌린지 인증 여부 응답 객체")
data class FindUserChallengeIsProveResponse(

    @Schema(description = "챌린지 인증 여부", example = "true")
    val isProve: Boolean,
) {

    companion object {

        fun of(isProve: Boolean) = FindUserChallengeIsProveResponse(isProve)
    }
}
