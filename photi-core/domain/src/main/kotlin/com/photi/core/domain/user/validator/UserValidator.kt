package com.photi.core.domain.user.validator

import com.photi.core.domain.common.consts.UnavailableConsts
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.user.dto.ChangePasswordDto
import com.photi.core.domain.user.model.User
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.user.query.UserQueryService
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userQueryService: UserQueryService,
    private val passwordPort: PasswordPort,
) {

    fun validateEmail(user: User, email: String) {
        validateIsDeleted(user)
        validateExistsActiveUserBy(email)
    }

    fun validateAuthenticationCode(user: User?, authenticationCode: String) {
        user ?: throw CustomException(ExceptionCode.EMAIL_NOT_FOUND)
        validateMatches(user, authenticationCode)
    }

    fun validateUsername(username: String) {
        validateIsInUnavailable(username)
        validateExistsUserBy(username)
    }

    fun validatePassword(user: User, password: String) {
        validateIsDeleted(user)
        passwordPort.validateMatches(password, user.password!!)
    }

    fun validateNewPassword(user: User, dto: ChangePasswordDto) {
        passwordPort.validateMatches(dto.password, user.password!!)
        validateMatches(dto.newPassword, dto.reEnteredNewPassword)
    }

    fun validateNewUser(email: String, username: String) {
        if (userQueryService.existsActiveUserBy(email)) {
            throw CustomException(ExceptionCode.EXISTING_USER)
        }
        validateUsername(username)
    }

    private fun validateIsDeleted(user: User) {
        if (user.isDeleted) {
            throw CustomException(ExceptionCode.DELETED_USER)
        }
    }

    private fun validateExistsActiveUserBy(email: String) {
        if (userQueryService.existsActiveUserBy(email)) {
            throw CustomException(ExceptionCode.EXISTING_EMAIL)
        }
    }

    private fun validateMatches(user: User, authenticationCode: String) {
        if (user.authenticationCode != authenticationCode) {
            throw CustomException(ExceptionCode.EMAIL_VERIFICATION_CODE_INVALID)
        }
    }

    private fun validateIsInUnavailable(username: String) {
        if (username in UnavailableConsts.USERNAME.fields) {
            throw CustomException(ExceptionCode.UNAVAILABLE_USERNAME)
        }
    }

    private fun validateExistsUserBy(username: String) {
        if (userQueryService.existsUserBy(username)) {
            throw CustomException(ExceptionCode.EXISTING_USERNAME)
        }
    }

    private fun validateMatches(password: String, reEnteredPassword: String) {
        if (password != reEnteredPassword) {
            throw CustomException(ExceptionCode.PASSWORD_MATCH_INVALID)
        }
    }
}
