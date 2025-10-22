package com.photi.core.domain.challengemember.service

import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.challengemember.exception.ChallengeMemberException
import com.photi.core.domain.challengemember.service.query.ChallengeMemberQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeMemberService(
    private val challengeMemberQueryService: ChallengeMemberQueryService,
) {

    @Transactional
    fun registerChallengePersonalGoal(
        userId: Long,
        challengeId: Long,
        dto: RegisterChallengePersonalGoalDto,
    ) {
        val challengeMember = challengeMemberQueryService.getChallengeMemberBy(userId, challengeId)
            ?: throw ChallengeMemberException.NotFoundChallengeMemberException()
        challengeMember.registerGoal(dto)
    }

    fun findChallengeMembers(userId: Long, challengeId: Long) =
        challengeMemberQueryService.getChallengeMembersBy(userId, challengeId)
}
