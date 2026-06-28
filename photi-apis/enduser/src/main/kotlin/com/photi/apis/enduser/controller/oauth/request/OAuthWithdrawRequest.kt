package com.photi.apis.enduser.controller.oauth.request

import com.photi.core.domain.user.dto.OAuthWithdrawDto
import com.photi.core.domain.user.model.OAuthProviderType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "OAuth 회원 탈퇴 - 카카오, 구글 요청 객체")
data class OAuthWithdrawRequest(

    @Schema(
        description = "OAuth 타입",
        examples = ["KAKAO", "GOOGLE"],
    )
    @field:NotBlank(message = "OAuth 타입은 필수 입력입니다.")
    @field:Pattern(
        regexp = "KAKAO|GOOGLE",
        message = "OAuth 타입은 KAKAO, GOOGLE 중 하나여야 합니다.",
    )
    val provider: String,

    @Schema(description = "회원 번호", example = "12345678")
    @field:NotBlank(message = "회원 번호는 필수 입력입니다.")
    val sub: String,
) {

    fun toServiceDto() = OAuthWithdrawDto(OAuthProviderType.valueOf(provider), sub)
}
