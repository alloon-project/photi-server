package com.photi.core.domain.challengemember.service.command

import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.challengemember.model.ChallengeMember
import com.photi.core.domain.challengemember.model.repository.ChallengeMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ChallengeMemberCommandService(
    private val challengeMemberRepository: ChallengeMemberRepository,
) {

    fun createCreator(userId: Long, challengeId: Long) {
        val creator = ChallengeMember(userId = userId, challengeId = challengeId)
        challengeMemberRepository.save(creator)
    }

    fun createMember(userId: Long, challengeId: Long, dto: RegisterChallengePersonalGoalDto) {
        val member = ChallengeMember(userId = userId, challengeId = challengeId, isCreator = false)
        challengeMemberRepository.save(member)
        member.registerGoal(dto)
    }
}
