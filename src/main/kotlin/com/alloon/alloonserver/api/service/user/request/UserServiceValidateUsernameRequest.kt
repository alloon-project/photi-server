package com.alloon.alloonserver.api.service.user.request

import com.alloon.alloonserver.common.constant.RegexPatternConstants
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserServiceValidateUsernameRequest(
    @field:Size(min = 5, max = 20, message = "아이디는 5~20자만 가능합니다.")
    @field:Pattern(regexp = RegexPatternConstants.LOWERCASE_NUMBER_UNDERSCORE, message = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요.")
    var username: String,
)