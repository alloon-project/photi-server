package com.photi.apis.enduser.controller.oauth.response

import com.photi.core.domain.user.dto.OAuthLoginDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "OAuth 로그인 응답 객체")
data class OAuthLoginResponse(

    @Schema(description = "아이디", example = "null 또는 설정된 아이디")
    val username: String?,
) {

    companion object {

        fun of(user: OAuthLoginDto) = OAuthLoginResponse(user.username)
    }
}