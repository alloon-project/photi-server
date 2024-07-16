package com.alloon.alloonserver.service.user.response

import com.alloon.alloonserver.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 응답 객체")
data class UserGetInfoResponse(
    @Schema(description = "사용자 식별자", example = "1")
    val userId: Long,
    @Schema(description = "사용자 아이디", example = "photi")
    val username: String,
    @Schema(description = "사용자 프로필 이미지 url", example = "https://url.kr/5MhHhD")
    val imageUrl: String,
    @Schema(description = "사용자 이메일", example = "photi@photi.com")
    val email: String,
) {

    constructor(user: User) : this(user.id!!, user.username, user.imageUrl, user.contact.email)
}
