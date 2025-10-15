package com.photi.apis.enduser.controller.user.dto.request

import com.photi.core.domain.user.dto.FindImagePreSignedUrlDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "이미지 PresignedURL 조회 요청 객체")
data class FindImagePreSignedUrlRequest(

    @Schema(description = "이미지 이름", example = "image.jpg")
    @field:NotBlank(message = "이미지 이름은 필수 입력입니다.")
    val imageName: String,
) {

    fun toServiceDto() = FindImagePreSignedUrlDto(imageName)
}
