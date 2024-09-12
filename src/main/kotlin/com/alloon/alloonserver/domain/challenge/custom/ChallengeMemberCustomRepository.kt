package com.alloon.alloonserver.domain.challenge.custom

import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.service.challenge.dto.ChallengeMemberImageDto
import com.alloon.alloonserver.service.challenge.dto.FindChallengeMembersDto

interface ChallengeMemberCustomRepository {

    fun find(id: Long): ChallengeMember?

    fun findByUserIdAndChallengeId(userId: Long, challengeId: Long): ChallengeMember?

    fun findAllByChallengeId(userId: Long, challengeId: Long): List<FindChallengeMembersDto>

    fun findImagesByChallengeId(challengeId: Long): List<ChallengeMemberImageDto>
}