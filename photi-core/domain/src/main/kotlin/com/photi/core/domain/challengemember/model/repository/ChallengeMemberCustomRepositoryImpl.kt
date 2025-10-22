package com.photi.core.domain.challengemember.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeMembersDto
import com.photi.core.domain.challenge.dto.QFindChallengeMembersDto
import com.photi.core.domain.challengemember.model.QChallengeMember.challengeMember
import com.photi.core.domain.challengemember.model.StatusType.PROGRESS
import com.photi.core.domain.user.model.QUser.user
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ChallengeMemberCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ChallengeMemberCustomRepository {

    override fun findChallengeMembersById(
        userId: Long,
        challengeId: Long,
    ): List<FindChallengeMembersDto> {
        val spec = CaseBuilder()
            .`when`(challengeMember.isCreator.isTrue).then(1)
            .`when`(challengeMember.userId.eq(userId)).then(2)
            .otherwise(3)
        return queryFactory
            .select(
                QFindChallengeMembersDto(
                    challengeMember.id,
                    user.username,
                    user.imageUrl,
                    challengeMember.isCreator,
                    challengeMember.createdDateTime,
                    challengeMember.goal,
                )
            )
            .from(challengeMember)
            .join(user).on(user.id.eq(challengeMember.userId))
            .where(
                challengeMember.challengeId.eq(challengeId),
                challengeMember.status.eq(PROGRESS),
            )
            .orderBy(
                spec.asc(),
                challengeMember.createdDateTime.asc(),
            )
            .fetch()
    }
}
