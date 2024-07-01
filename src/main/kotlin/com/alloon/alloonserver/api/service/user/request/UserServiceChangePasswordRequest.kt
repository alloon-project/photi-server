package com.alloon.alloonserver.api.service.user.request

import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.LETTER_NUMBER_SPECIAL_CHARACTER
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "비밀번호 변경 요청 객체")
data class UserServiceChangePasswordRequest(
    @Schema(description = "기존 비밀번호", example = "password1!")
    var password: String,
    @field:Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.")
    @field:Pattern(regexp = LETTER_NUMBER_SPECIAL_CHARACTER, message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요.")
    @Schema(description = "새 비밀번호", example = "password2!")
    var newPassword: String,
    @Schema(description = "새 비밀번호 재입력", example = "password2!")
    var newPasswordReEnter: String,
)
