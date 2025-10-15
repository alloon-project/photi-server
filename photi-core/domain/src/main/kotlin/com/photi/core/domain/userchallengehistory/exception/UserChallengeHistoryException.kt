package com.photi.core.domain.userchallengehistory.exception

import com.photi.core.domain.common.exception.PhotiException

sealed class UserChallengeHistoryException(errorCode: UserChallengeHistoryErrorCode) :
    PhotiException(errorCode) {

    class ExceedsChallengeLimitException :
        UserChallengeHistoryException(UserChallengeHistoryErrorCode.CHALLENGE_LIMIT_EXCEED)
}
