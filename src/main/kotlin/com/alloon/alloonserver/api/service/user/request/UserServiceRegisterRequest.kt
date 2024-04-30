package com.alloon.alloonserver.api.service.user.request

import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.LETTER_NUMBER_SPECIAL_CHARACTER
import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.LOWERCASE_NUMBER_UNDERSCORE
import com.alloon.alloonserver.domain.user.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserServiceRegisterRequest(
    @field:Size(min = 1, max = 100, message = "이메일은 1~100자만 가능합니다.")
    @field:Email(message = "올바른 이메일 형식을 입력해 주세요.")
    var email: String,
    var verificationCode: String,
    @field:Size(min = 5, max = 20, message = "아이디는 5~20자만 가능합니다.")
    @field:Pattern(regexp = LOWERCASE_NUMBER_UNDERSCORE, message = "아이디는 소문자 영어, 숫자, 특수문자(_)의 조합으로 입력해 주세요.")
    var username: String,
    @field:Size(min = 8, max = 30, message = "비밀번호는 8~30자만 가능합니다.")
    @field:Pattern(regexp = LETTER_NUMBER_SPECIAL_CHARACTER, message = "비밀번호는 영어, 숫자, 특수문자(#$@!%&*)의 조합으로 입력해 주세요.")
    var password: String,
    var passwordReEnter: String,
) {
    fun toUserEntity(contact: Contact, userTemplateImage: String): User {
        return User(contact = contact, username = username, password = password, imageUrl = userTemplateImage)
    }

    fun toUserRoleEntity(user: User): UserRole {
        return UserRole(user = user, role = Role.USER)
    }
}
