package com.photi.apis.enduser.config.security

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.user.port.PasswordPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordAdapter(
    private val passwordEncoder: PasswordEncoder,
) : PasswordPort {

    override fun encode(password: String): String = passwordEncoder.encode(password)

    override fun validateMatches(password: String, encodedPassword: String) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw CustomException(ExceptionCode.LOGIN_UNAUTHENTICATED)
        }
    }
}
