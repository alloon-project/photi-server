package com.photi.server.domain.challenge.custom

import com.photi.server.domain.challenge.ChallengeMember
import com.photi.server.service.challenge.dto.ChallengeMemberImageDto
import com.photi.server.service.challenge.dto.FindChallengeMembersDto

interface ChallengeMemberCustomRepository {

    fun find(id: Long): ChallengeMember?

    fun findByUserIdAndChallengeId(userId: Long, challengeId: Long): ChallengeMember?

    fun findAllByChallengeId(userId: Long, challengeId: Long): List<FindChallengeMembersDto>

    fun findImagesByChallengeId(challengeId: Long): List<ChallengeMemberImageDto>
}