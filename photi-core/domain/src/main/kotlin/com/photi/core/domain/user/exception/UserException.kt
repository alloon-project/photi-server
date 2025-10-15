package com.photi.core.domain.user.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class UserException(errorCode: UserErrorCode) : PhotiException(errorCode) {

    class InvalidEmailException : UserException(UserErrorCode.EMAIL_VALIDATION_INVALID)

    class InvalidEmailAuthenticationCodeException :
        UserException(UserErrorCode.EMAIL_VERIFICATION_CODE_INVALID)

    class InvalidPasswordMatchException : UserException(UserErrorCode.PASSWORD_MATCH_INVALID)

    class UnauthorizedLoginException : UserException(UserErrorCode.LOGIN_UNAUTHENTICATED)

    class NotFoundEmailException : UserException(UserErrorCode.EMAIL_NOT_FOUND)

    class NotFoundUserException : UserException(UserErrorCode.USER_NOT_FOUND)

    class ExistsUserException : UserException(UserErrorCode.EXISTING_USER)

    class ExistsEmailException : UserException(UserErrorCode.EXISTING_EMAIL)

    class ExistsDeletedUserException : UserException(UserErrorCode.DELETED_USER)

    class ExistsUsernameException : UserException(UserErrorCode.EXISTING_USERNAME)

    class NotAvailableUsernameException : UserException(UserErrorCode.UNAVAILABLE_USERNAME)
}
