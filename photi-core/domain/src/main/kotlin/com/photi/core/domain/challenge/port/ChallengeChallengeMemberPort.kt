package com.photi.core.domain.challenge.port

import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto

interface ChallengeChallengeMemberPort {

    fun createCreator(userId: Long, challengeId: Long)

    fun createMember(userId: Long, challengeId: Long, dto: RegisterChallengePersonalGoalDto)

    fun getChallengeMemberIdBy(userId: Long, challengeId: Long): Long

    fun validateIsCreator(userId: Long, challengeId: Long)

    fun withdrawMember(userId: Long, challengeId: Long)
}
