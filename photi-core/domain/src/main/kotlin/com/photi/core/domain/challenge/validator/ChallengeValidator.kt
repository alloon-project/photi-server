package com.photi.core.domain.challenge.validator

import com.photi.core.domain.challenge.query.ChallengeQueryService
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import org.springframework.stereotype.Component

@Component
class ChallengeValidator(
    private val challengeQueryService: ChallengeQueryService,
) {

    fun validateExistsBy(targetId: Long) {
        if (!challengeQueryService.existsBy(targetId)) {
            throw CustomException(ExceptionCode.CHALLENGE_NOT_FOUND)
        }
    }
}
