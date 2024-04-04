package com.alloon.alloonserver.api.service.user.request

import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.LETTER_NUMBER_SPECIAL_CHARACTER
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserServiceChangePasswordRequest(
    var password: String,
    @field:Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.")
    @field:Pattern(regexp = LETTER_NUMBER_SPECIAL_CHARACTER, message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요.")
    var newPassword: String,
    var newPasswordReEntered: String,
)
