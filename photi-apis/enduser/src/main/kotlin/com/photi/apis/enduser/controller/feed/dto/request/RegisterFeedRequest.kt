package com.photi.apis.enduser.controller.feed.dto.request

import com.photi.core.domain.feed.dto.RegisterFeedDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "피드 등록 요청 객체")
data class RegisterFeedRequest(

    @Schema(description = "피드 이미지 pre signed url", example = "https://url.kr/5MhHhD")
    val preSignedUrl: String,
) {

    fun toServiceDto() = RegisterFeedDto(preSignedUrl.substringBefore(SUFFIX))

    companion object {
        private const val SUFFIX = "?"
    }
}
