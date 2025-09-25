package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.dto.ChallengeMemberImageDto
import com.photi.core.domain.challenge.dto.FindChallengeMembersDto
import com.photi.core.domain.challenge.dto.FindCreatorDto
import com.photi.core.domain.challenge.model.ChallengeMember

interface ChallengeMemberCustomRepository {

    fun find(id: Long): ChallengeMember?

    fun findByUserIdAndChallengeId(userId: Long, challengeId: Long): ChallengeMember?

    fun findAllByChallengeId(userId: Long, challengeId: Long): List<FindChallengeMembersDto>

    fun findImagesByChallengeId(challengeId: Long): List<ChallengeMemberImageDto>

    fun findCreatorByChallengeId(challengeId: Long): FindCreatorDto
}
