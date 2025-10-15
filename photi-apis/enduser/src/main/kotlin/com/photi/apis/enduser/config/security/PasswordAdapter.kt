package com.photi.apis.enduser.config.security

import com.photi.core.domain.user.exception.UserException
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
            throw UserException.UnauthorizedLoginException()
        }
    }
}
