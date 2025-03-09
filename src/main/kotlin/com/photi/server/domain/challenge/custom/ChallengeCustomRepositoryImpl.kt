package com.photi.server.domain.challenge.custom

import com.photi.server.domain.base.ServiceStatus
import com.photi.server.domain.base.ServiceStatus.ACTIVE
import com.photi.server.domain.challenge.Challenge
import com.photi.server.domain.challenge.ChallengeMemberStatus
import com.photi.server.domain.challenge.ChallengeMemberStatus.PROGRESS
import com.photi.server.domain.challenge.QChallenge.challenge
import com.photi.server.domain.challenge.QChallengeHashtag.challengeHashtag
import com.photi.server.domain.challenge.QChallengeMember.challengeMember
import com.photi.server.service.challenge.dto.*
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
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
        val popularChallenges = queryFactory
            .select(
                QFindPopularChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.goal,
                    challenge.currentMemberCnt,
                    challenge.proveTime,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .where(eqServiceStatus(ACTIVE))
            .orderBy(challenge.visitCnt.desc())
            .limit(5)
            .fetch()
        val challengeIds = popularChallenges.map { it.id }
        val hashtags = queryFactory
            .select(
                QFindChallengeHashtagDto(
                    challengeHashtag.challenge.id,
                    challengeHashtag.hashtag
                )
            )
            .from(challengeHashtag)
            .where(challengeHashtag.challenge.id.`in`(challengeIds))
            .fetch()
            .groupBy { it.challengeId }

        popularChallenges.forEach {
            it.hashtags = hashtags[it.id] ?: emptyList()
            it.memberImages = queryFactory
                .select(challengeMember.user.imageUrl)
                .from(challengeMember)
                .where(challengeMember.challenge.id.eq(it.id), eqChallengeMemberStatus(PROGRESS))
                .orderBy(challengeMember.createDateTime.desc())
                .limit(3)
                .fetch()
        }

        return popularChallenges
    }

    override fun findInfoById(id: Long): Challenge? {
        return queryFactory
            .selectFrom(challenge)
            .join(challenge.rules).fetchJoin()
            .where(challenge.id.eq(id))
            .fetchFirst()
    }

    override fun findAllOrderByEndDate(pageable: Pageable): Slice<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.endDate,
                    challenge.imageUrl,
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .where(eqServiceStatus(ACTIVE))
            .orderBy(challenge.endDate.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        val challengeIds = content.map { it.id }
        val hashtags = queryFactory
            .select(
                QFindChallengeHashtagDto(
                    challengeHashtag.challenge.id,
                    challengeHashtag.hashtag
                )
            )
            .from(challengeHashtag)
            .where(challengeHashtag.challenge.id.`in`(challengeIds))
            .fetch()
            .groupBy { it.challengeId }

        content.forEach {
            it.hashtags = hashtags[it.id] ?: emptyList()
        }

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        return SliceImpl(content, pageable, hasNext)
    }

    override fun findInvitationCodeById(id: Long): FindChallengeInvitationCodeDto? {
        return queryFactory
            .select(
                QFindChallengeInvitationCodeDto(
                    challenge.name,
                    challenge.invitationCode
                )
            )
            .from(challenge)
            .where(challenge.id.eq(id))
            .fetchOne()
    }

    override fun findAllByHashtag(
        hashtag: String?,
        popularHashtags: List<String>?,
        pageable: Pageable
    ): Slice<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val query = queryFactory
            .select(
                QFindChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.endDate,
                    challenge.imageUrl,
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .join(challengeHashtag).on(challengeHashtag.challenge.id.eq(challenge.id))
            .where(eqServiceStatus(ACTIVE))

        if (!hashtag.isNullOrBlank()) {
            query.where(challengeHashtag.hashtag.eq(hashtag))
        } else {
            query.where(challengeHashtag.hashtag.`in`(popularHashtags))
        }

        val content = query
            .groupBy(challenge.id)
            .orderBy(challenge.currentMemberCnt.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        val challengeIds = content.map { it.id }
        val hashtags = queryFactory
            .select(
                QFindChallengeHashtagDto(
                    challengeHashtag.challenge.id,
                    challengeHashtag.hashtag
                )
            )
            .from(challengeHashtag)
            .where(challengeHashtag.challenge.id.`in`(challengeIds))
            .fetch()
            .groupBy { it.challengeId }

        content.forEach {
            it.hashtags = hashtags[it.id] ?: emptyList()
        }

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        return SliceImpl(content, pageable, hasNext)
    }

    override fun searchByName(name: String, pageable: Pageable): Slice<SearchChallengeByNameDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QSearchChallengeByNameDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.currentMemberCnt,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .where(eqServiceStatus(ACTIVE), challenge.name.containsIgnoreCase(name))
            .orderBy(
                Expressions.numberTemplate(
                    Int::class.java,
                    "case when {0} = {1} then 1 when {0} like {2} then 2 else 3 end",
                    challenge.name, name, "%$name%",
                ).asc(),
                challenge.endDate.desc(),
            )
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        content.forEach {
            it.memberImages = queryFactory
                .select(challengeMember.user.imageUrl)
                .from(challengeMember)
                .where(challengeMember.challenge.id.eq(it.id), eqChallengeMemberStatus(PROGRESS))
                .orderBy(challengeMember.createDateTime.desc())
                .limit(3)
                .fetch()
        }

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        return SliceImpl(content, pageable, hasNext)
    }

    override fun searchByHashtag(
        hashtag: String,
        pageable: Pageable
    ): Slice<SearchChallengeByHashtagDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QSearchChallengeByHashtagDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.currentMemberCnt,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .join(challenge.hashtags, challengeHashtag)
            .where(eqServiceStatus(ACTIVE), challengeHashtag.hashtag.containsIgnoreCase(hashtag))
            .orderBy(
                Expressions.numberTemplate(
                    Int::class.java,
                    "case when {0} = {1} then 1 when {0} like {2} then 2 else 3 end",
                    challengeHashtag.hashtag, hashtag, "%$hashtag%"
                ).asc(),
                challenge.endDate.desc(),
            )
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        val challengeIds = content.map { it.id }
        val hashtags = queryFactory
            .select(
                QFindChallengeHashtagDto(
                    challengeHashtag.challenge.id,
                    challengeHashtag.hashtag
                )
            )
            .from(challengeHashtag)
            .where(challengeHashtag.challenge.id.`in`(challengeIds))
            .fetch()
            .groupBy { it.challengeId }

        content.forEach {
            it.hashtags = hashtags[it.id] ?: emptyList()
            it.memberImages = queryFactory
                .select(challengeMember.user.imageUrl)
                .from(challengeMember)
                .where(challengeMember.challenge.id.eq(it.id), eqChallengeMemberStatus(PROGRESS))
                .orderBy(challengeMember.createDateTime.desc())
                .limit(3)
                .fetch()
        }

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

    private fun eqChallengeMemberStatus(status: ChallengeMemberStatus): BooleanExpression =
        challengeMember.status.eq(status)
}