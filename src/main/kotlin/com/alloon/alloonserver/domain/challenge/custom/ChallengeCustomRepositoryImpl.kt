package com.alloon.alloonserver.domain.challenge.custom

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.QChallenge.challenge
import com.alloon.alloonserver.service.challenge.dto.FindChallengesDto
import com.alloon.alloonserver.service.challenge.dto.FindPopularChallengesDto
import com.alloon.alloonserver.service.challenge.dto.QFindChallengesDto
import com.alloon.alloonserver.service.challenge.dto.QFindPopularChallengesDto
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class ChallengeCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ChallengeCustomRepository {

    override fun find(id: Long): Challenge? {
        return queryFactory
            .selectFrom(challenge)
            .where(
                challenge.id.eq(id),
                eqServiceStatus(ACTIVE)
            ).fetchFirst()
    }

    override fun findPopular(): List<FindPopularChallengesDto> {
        return queryFactory
            .select(
                QFindPopularChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.goal,
                    challenge.proveTime,
                    challenge.endDate,
                    challenge.hashtags,
                )
            )
            .from(challenge)
            .where(eqServiceStatus(ACTIVE))
            .orderBy(challenge.visitCnt.desc())
            .limit(5)
            .fetch()
    }

    override fun findInfoById(id: Long): Challenge? {
        return queryFactory
            .selectFrom(challenge)
            .join(challenge.rules).fetchJoin()
            .where(challenge.id.eq(id))
            .fetchFirst()
    }

    override fun findAllOrderByStartDate(pageable: Pageable): Slice<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.endDate,
                    challenge.imageUrl,
                    challenge.hashtags
                )
            )
            .from(challenge)
            .where(eqServiceStatus(ACTIVE))
            .orderBy(challenge.startDate.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        return SliceImpl(content, pageable, hasNext)
    }

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { challenge.serviceStatus.eq(serviceStatus) }
}