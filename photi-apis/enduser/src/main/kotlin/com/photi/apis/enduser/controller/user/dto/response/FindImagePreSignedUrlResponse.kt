package com.photi.apis.enduser.controller.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이미지 PresignedURL 조회 응답 객체")
data class FindImagePreSignedUrlResponse(

    @Schema(
        description = "이미지 PresignedURL",
        example = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/test.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240302T073447Z&X-Amz-SignedHeaders=host&X-Amz-Expires=158&X-Amz-Credential=111&X-Amz-Signature=1b1629",
    )
    val preSignedUrl: String,
) {

    companion object {

        fun of(preSignedUrl: String) = FindImagePreSignedUrlResponse(preSignedUrl)
    }
}
