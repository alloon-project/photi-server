package com.photi.core.domain.challengemember.model.repository

import com.photi.core.domain.challengemember.model.ChallengeMember
import com.photi.core.domain.challengemember.model.StatusType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChallengeMemberRepository : JpaRepository<ChallengeMember, Long>,
    ChallengeMemberCustomRepository {

    fun existsByIdAndStatus(id: Long, status: StatusType): Boolean

    fun existsByUserIdAndChallengeIdAndStatus(
        userId: Long,
        challengeId: Long,
        status: StatusType,
    ): Boolean

    @Query("select cm from ChallengeMember cm where cm.userId = :userId and cm.challengeId = :challengeId and cm.status = 'PROGRESS'")
    fun findProgressMemberByUserIdAndChallengeId(userId: Long, challengeId: Long): ChallengeMember?
}
