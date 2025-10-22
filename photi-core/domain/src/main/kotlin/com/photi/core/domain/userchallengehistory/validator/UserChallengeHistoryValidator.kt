package com.photi.core.domain.userchallengehistory.validator

import com.photi.core.domain.userchallengehistory.exception.UserChallengeHistoryException
import com.photi.core.domain.userchallengehistory.model.UserChallengeHistory
import org.springframework.stereotype.Component

@Component
class UserChallengeHistoryValidator {

    fun validateJoinChallengeCount(userChallengeHistory: UserChallengeHistory) {
        if (userChallengeHistory.challengeCount >= JOIN_CHALLENGE_LIMIT) {
            throw UserChallengeHistoryException.ExceedsChallengeLimitException()
        }
    }

    companion object {
        private const val JOIN_CHALLENGE_LIMIT = 20
    }
}
