package com.photi.apis.enduser.controller.auth.dto.request

import com.photi.core.domain.common.consts.RegexPattern.LETTER_NUMBER_SPECIAL_CHARACTER
import com.photi.core.domain.common.consts.RegexPattern.LOWERCASE_NUMBER_UNDERSCORE
import com.photi.core.domain.user.dto.SignUpRequestDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "회원가입 요청 객체")
data class SignUpRequest(

    @Schema(description = "이메일", example = "photi@photi.com")
    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    @field:Size(min = 1, max = 100, message = "이메일은 1~100자만 가능합니다.")
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    val email: String,

    @Schema(description = "아이디", example = "photi")
    @field:NotBlank(message = "아이디는 필수 입력입니다.")
    @field:Size(min = 5, max = 20, message = "아이디는 5~20자만 가능합니다.")
    @field:Pattern(
        regexp = LOWERCASE_NUMBER_UNDERSCORE,
        message = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요."
    )
    val username: String,

    @Schema(description = "비밀번호", example = "password1!")
    @field:NotBlank(message = "비밀번호는 필수 입력입니다.")
    @field:Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.")
    @field:Pattern(
        regexp = LETTER_NUMBER_SPECIAL_CHARACTER,
        message = "비밀번호는 영어, 숫자, 특수문자의 조합으로 입력해 주세요."
    )
    val password: String,
) {

    fun toServiceDto() = SignUpRequestDto(email, username, password)
}
