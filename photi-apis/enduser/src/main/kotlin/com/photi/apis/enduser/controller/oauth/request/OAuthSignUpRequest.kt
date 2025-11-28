package com.photi.apis.enduser.controller.oauth.request

import com.photi.core.domain.common.consts.RegexPattern.LOWERCASE_NUMBER_UNDERSCORE
import com.photi.core.domain.user.dto.OAuthSignUpDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "OAuth 회원가입 요청 객체")
data class OAuthSignUpRequest(

    @Schema(description = "아이디", example = "photi")
    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    @field:Size(min = 5, max = 20, message = "아이디는 5~20자만 가능합니다.")
    @field:Pattern(
        regexp = LOWERCASE_NUMBER_UNDERSCORE,
        message = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요."
    )
    val username: String,
) {

    fun toServiceDto() = OAuthSignUpDto(username)
}
