package com.alloon.alloonserver.domain.challenge.custom

import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.challenge.ChallengeMemberStatus
import com.alloon.alloonserver.domain.challenge.ChallengeMemberStatus.PROGRESS
import com.alloon.alloonserver.domain.challenge.QChallengeMember.challengeMember
import com.alloon.alloonserver.service.challenge.dto.ChallengeMemberImageDto
import com.alloon.alloonserver.service.challenge.dto.FindChallengeMembersDto
import com.alloon.alloonserver.service.challenge.dto.QChallengeMemberImageDto
import com.alloon.alloonserver.service.challenge.dto.QFindChallengeMembersDto
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ChallengeMemberCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ChallengeMemberCustomRepository {

    override fun find(id: Long): ChallengeMember? {
        return queryFactory
            .selectFrom(challengeMember)
            .where(
                challengeMember.id.eq(id),
                eqChallengeMemberStatus(PROGRESS)
            ).fetchFirst()
    }

    override fun findByUserIdAndChallengeId(userId: Long, challengeId: Long): ChallengeMember? {
        return queryFactory
            .selectFrom(challengeMember)
            .join(challengeMember.user).fetchJoin()
            .join(challengeMember.challenge).fetchJoin()
            .where(
                challengeMember.user.id.eq(userId),
                challengeMember.challenge.id.eq(challengeId),
                eqChallengeMemberStatus(PROGRESS)
            )
            .fetchFirst()
    }

    override fun findAllByChallengeId(
        userId: Long,
        challengeId: Long
    ): List<FindChallengeMembersDto> {
        val spec = CaseBuilder()
            .`when`(challengeMember.isCreator.isTrue).then(1)
            .`when`(challengeMember.user.id.eq(userId)).then(2)
            .otherwise(3)

        return queryFactory
            .select(
                QFindChallengeMembersDto(
                    challengeMember.id,
                    challengeMember.user.username,
                    challengeMember.user.imageUrl,
                    challengeMember.isCreator,
                    challengeMember.createDateTime,
                    challengeMember.goal
                )
            )
            .from(challengeMember)
            .where(challengeMember.challenge.id.eq(challengeId), eqChallengeMemberStatus(PROGRESS))
            .orderBy(
                spec.asc(),
                challengeMember.createDateTime.asc()
            )
            .fetch()
    }

    override fun findImagesByChallengeId(challengeId: Long): List<ChallengeMemberImageDto> {
        return queryFactory
            .select(QChallengeMemberImageDto(challengeMember.user.imageUrl))
            .from(challengeMember)
            .where(challengeMember.challenge.id.eq(challengeId), eqChallengeMemberStatus(PROGRESS))
            .orderBy(challengeMember.createDateTime.desc())
            .limit(3)
            .fetch()
    }

    private fun eqChallengeMemberStatus(status: ChallengeMemberStatus): BooleanExpression =
        challengeMember.status.eq(status)
}