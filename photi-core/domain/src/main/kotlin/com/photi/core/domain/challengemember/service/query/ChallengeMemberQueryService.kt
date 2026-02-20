package com.photi.core.domain.challengemember.service.query

import com.photi.core.domain.challengemember.model.StatusType
import com.photi.core.domain.challengemember.model.repository.ChallengeMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeMemberQueryService(
    private val challengeMemberRepository: ChallengeMemberRepository,
) {

    fun existsBy(id: Long) =
        challengeMemberRepository.existsByIdAndStatus(id, StatusType.PROGRESS)

    fun existsMemberBy(userId: Long, challengeId: Long) =
        challengeMemberRepository.existsByUserIdAndChallengeIdAndStatus(
            userId,
            challengeId,
            StatusType.PROGRESS,
        )

    fun getChallengeMemberIdBy(userId: Long, challengeId: Long) =
        challengeMemberRepository.findProgressMemberByUserIdAndChallengeId(userId, challengeId)?.id

    fun getChallengeMemberBy(userId: Long, challengeId: Long) =
        challengeMemberRepository.findProgressMemberByUserIdAndChallengeId(userId, challengeId)

    fun getChallengeMembersBy(userId: Long, challengeId: Long) =
        challengeMemberRepository.findChallengeMembersById(userId, challengeId)
}
