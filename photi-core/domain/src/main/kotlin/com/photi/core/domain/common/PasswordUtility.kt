package com.photi.core.domain.common

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordUtility(
    private val passwordEncoder: PasswordEncoder,
) {

    fun encryptPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun verifyPassword(password: String, encodedPassword: String) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw CustomException(ExceptionCode.LOGIN_UNAUTHENTICATED)
        }
    }
}
