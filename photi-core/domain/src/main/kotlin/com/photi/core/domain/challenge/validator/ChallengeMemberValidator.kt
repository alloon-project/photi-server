package com.photi.core.domain.challenge.validator

import com.photi.core.domain.challenge.query.ChallengeMemberQueryService
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import org.springframework.stereotype.Component

@Component
class ChallengeMemberValidator(
    private val challengeMemberQueryService: ChallengeMemberQueryService,
) {

    fun validateExistsBy(targetId: Long) {
        if (!challengeMemberQueryService.existsBy(targetId)) {
            throw CustomException(ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND)
        }
    }
}
