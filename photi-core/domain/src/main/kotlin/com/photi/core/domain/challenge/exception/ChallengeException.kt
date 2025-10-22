package com.photi.core.domain.challenge.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class ChallengeException(errorCode: ChallengeErrorCode) : PhotiException(errorCode) {

    class NotFoundChallengeException : ChallengeException(ChallengeErrorCode.CHALLENGE_NOT_FOUND)
}
