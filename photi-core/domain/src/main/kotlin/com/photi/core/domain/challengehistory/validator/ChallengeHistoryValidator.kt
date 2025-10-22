package com.photi.core.domain.challengehistory.validator

import com.photi.core.domain.challengehistory.model.ChallengeHistory
import org.springframework.stereotype.Component

@Component
class ChallengeHistoryValidator {

    fun validateLastChallengeMember(challengeHistory: ChallengeHistory) =
        challengeHistory.challengeMemberCount == COUNT

    companion object {
        private const val COUNT = 1
    }
}
