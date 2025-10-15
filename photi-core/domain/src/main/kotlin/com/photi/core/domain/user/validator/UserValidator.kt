package com.photi.core.domain.user.validator

import com.photi.core.domain.common.consts.NotAvailable
import com.photi.core.domain.user.dto.ChangePasswordDto
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.model.User
import com.photi.core.domain.user.port.PasswordPort
import com.photi.core.domain.user.service.query.UserQueryService
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
        user ?: throw UserException.NotFoundEmailException()
        validateMatches(user, authenticationCode)
    }

    fun validateUsername(username: String) {
        validateIsInNotAvailable(username)
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
            throw UserException.ExistsUserException()
        }
        validateUsername(username)
    }

    private fun validateIsDeleted(user: User) {
        if (user.isDeleted) {
            throw UserException.ExistsDeletedUserException()
        }
    }

    private fun validateExistsActiveUserBy(email: String) {
        if (userQueryService.existsActiveUserBy(email)) {
            throw UserException.ExistsEmailException()
        }
    }

    private fun validateMatches(user: User, authenticationCode: String) {
        if (user.authenticationCode != authenticationCode) {
            throw UserException.InvalidEmailAuthenticationCodeException()
        }
    }

    private fun validateIsInNotAvailable(username: String) {
        if (username in NotAvailable.USERNAME.fields) {
            throw UserException.NotAvailableUsernameException()
        }
    }

    private fun validateExistsUserBy(username: String) {
        if (userQueryService.existsUserBy(username)) {
            throw UserException.ExistsUsernameException()
        }
    }

    private fun validateMatches(password: String, reEnteredPassword: String) {
        if (password != reEnteredPassword) {
            throw UserException.InvalidPasswordMatchException()
        }
    }
}
