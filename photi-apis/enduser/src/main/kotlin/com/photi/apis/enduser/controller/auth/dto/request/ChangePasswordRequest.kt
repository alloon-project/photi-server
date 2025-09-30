package com.photi.apis.enduser.controller.auth.dto.request

import com.photi.core.domain.common.consts.RegexPatternConstants.LETTER_NUMBER_SPECIAL_CHARACTER
import com.photi.core.domain.user.dto.ChangePasswordDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "비밀번호 변경 요청 객체")
data class ChangePasswordRequest(

    @Schema(description = "기존 비밀번호", example = "password1!")
    @field:NotBlank(message = "비밀번호는 필수 업력입니다.")
    val password: String,

    @Schema(description = "새 비밀번호", example = "password2!")
    @field:NotBlank(message = "새 비밀번호는 필수 입력입니다.")
    @field:Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.")
    @field:Pattern(
        regexp = LETTER_NUMBER_SPECIAL_CHARACTER,
        message = "비밀번호는 영어, 숫자, 특수문자의 조합으로 입력해 주세요."
    )
    val newPassword: String,

    @Schema(description = "새 비밀번호 재입력", example = "password2!")
    @field:NotBlank(message = "새 비밀번호 재입력은 필수 입력입니다.")
    val newPasswordReEnter: String,
) {

    fun toServiceDto() = ChangePasswordDto(password, newPassword, newPasswordReEnter)
}
