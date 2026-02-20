package com.photi.core.domain.challengemember.validator

import com.photi.core.domain.challengemember.exception.ChallengeMemberException
import com.photi.core.domain.challengemember.model.ChallengeMember
import com.photi.core.domain.challengemember.service.query.ChallengeMemberQueryService
import org.springframework.stereotype.Component

@Component
class ChallengeMemberValidator(
    private val challengeMemberQueryService: ChallengeMemberQueryService,
) {

    fun validateExistsBy(targetId: Long) {
        if (!challengeMemberQueryService.existsBy(targetId)) {
            throw ChallengeMemberException.NotFoundChallengeMemberException()
        }
    }

    fun validateExistsMemberBy(userId: Long, challengeId: Long) {
        if (challengeMemberQueryService.existsMemberBy(userId, challengeId)) {
            throw ChallengeMemberException.ExistsChallengeMemberException()
        }
    }

    fun validateIsCreator(challengeMember: ChallengeMember) {
        if (!challengeMember.isCreator) {
            throw ChallengeMemberException.ForbiddenCreatorException()
        }
    }
}
