package com.photi.apis.enduser.controller.user.dto.response

import com.photi.core.domain.user.dto.FindInfoDto
import com.photi.core.domain.user.model.OAuthProviderType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 응답 객체")
data class FindInfoResponse(

    @Schema(description = "사용자 프로필 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,

    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,

    @Schema(description = "사용자 이메일", example = "photi@photi.com")
    val email: String,

    @Schema(description = "OAuth provider", example = "GOOGLE")
    val provider: OAuthProviderType?,
) {

    companion object {

        fun of(info: FindInfoDto) = FindInfoResponse(
            info.imageUrl,
            info.username,
            info.email,
            info.provider,
        )
    }
}
