package com.photi.apis.enduser.controller.user.dto.request

import com.photi.core.domain.user.dto.UpdateProfileImageDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 프로필 업데이트 요청 객체")
data class UpdateProfileImageRequest(

    @Schema(description = "챌린지 이미지 pre signed url", example = "https://url.kr/5MhHhD")
    val preSignedUrl: String,
) {

    fun toServiceDto() = UpdateProfileImageDto(preSignedUrl.substringBefore(SUFFIX))

    companion object {
        private const val SUFFIX = "?"
    }
}
