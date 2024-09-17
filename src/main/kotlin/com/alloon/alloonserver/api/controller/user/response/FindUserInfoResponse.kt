package com.alloon.alloonserver.api.controller.user.response

import com.alloon.alloonserver.service.user.dto.FindUserInfoDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 응답 객체")
data class FindUserInfoResponse(

    @Schema(description = "사용자 프로필 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,

    @Schema(description = "사용자 이메일", example = "photi@photi.com")
    val email: String,
) {

    companion object {

        fun of(userInfo: FindUserInfoDto): FindUserInfoResponse {
            return FindUserInfoResponse(
                userInfo.imageUrl,
                userInfo.username,
                userInfo.email
            )
        }
    }
}