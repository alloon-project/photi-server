package com.photi.core.domain.common.exception

sealed class GlobalException(errorCode: GlobalErrorCode) : PhotiException(errorCode) {

    class SendEmailException : GlobalException(GlobalErrorCode.EMAIL_SEND_ERROR)

    class InvalidTokenException : GlobalException(GlobalErrorCode.INVALID_TOKEN)

    class ExpiredTokenException : GlobalException(GlobalErrorCode.EXPIRED_TOKEN)

    class PrivateKeyException : GlobalException(GlobalErrorCode.PRIVATE_KEY_ERROR)
}
