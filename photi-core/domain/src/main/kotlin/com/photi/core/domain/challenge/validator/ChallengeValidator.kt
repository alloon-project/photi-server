package com.photi.core.domain.challenge.validator

import com.photi.core.domain.challenge.exception.ChallengeException
import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.service.query.ChallengeQueryService
import org.springframework.stereotype.Component

@Component
class ChallengeValidator(
    private val challengeQueryService: ChallengeQueryService,
) {

    fun validateExistsBy(targetId: Long) {
        if (!challengeQueryService.existsBy(targetId)) {
            throw ChallengeException.NotFoundChallengeException()
        }
    }

    fun validateMatches(challenge: Challenge, invitationCode: String) =
        challenge.invitationCode == invitationCode
}
