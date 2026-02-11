package com.photi.apis.enduser.controller.user.dto.response

import com.photi.core.domain.user.dto.UpdateProfileImageDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 프로필 이미지 업데이트 응답 객체")
data class UpdateProfileImageResponse(

    @Schema(description = "사용자 프로필 이미지", example = "https://url.kr/5MhHhD")
    val imageUrl: String,
) {

    companion object {

        fun of(profileImage: UpdateProfileImageDto) =
            UpdateProfileImageResponse(profileImage.imageUrl)
    }
}
