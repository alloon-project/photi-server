package com.photi.core.domain.challengemember.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeMembersDto

interface ChallengeMemberCustomRepository {

    fun findChallengeMembersById(userId: Long, challengeId: Long): List<FindChallengeMembersDto>
}
