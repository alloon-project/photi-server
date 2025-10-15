package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.dto.*
import com.photi.core.domain.challenge.model.QChallenge.challenge
import com.photi.core.domain.challenge.model.QChallengeHashtag.challengeHashtag
import com.photi.core.domain.challenge.model.QChallengeRule.challengeRule
import com.photi.core.domain.challengehistory.model.QChallengeHistory.challengeHistory
import com.photi.core.domain.challengemember.model.QChallengeMember.challengeMember
import com.photi.core.domain.challengemember.model.StatusType.PROGRESS
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.challenge.model.StatusType.ACTIVE
import com.photi.core.domain.common.toSliceDto
import com.photi.core.domain.user.dto.QMemberImageDto
import com.photi.core.domain.user.model.QUser.user
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class ChallengeCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ChallengeCustomRepository {

    override fun findPopularChallenges(): List<FindPopularChallengesDto> {
        val challenges = queryFactory
            .select(
                QFindPopularChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.goal,
                    challengeHistory.challengeMemberCount,
                    challenge.proveTime,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .join(challengeHistory).on(challengeHistory.challengeId.eq(challenge.id))
            .where(challenge.status.eq(ACTIVE))
            .orderBy(challengeHistory.visitCount.desc())
            .limit(5)
            .fetch()
        val challengeIds = challenges.map { it.id }
        if (challengeIds.isNotEmpty()) {
            val hashtags = getHashtags(challengeIds)
            val memberImages = getMemberImages(challengeIds)
            challenges.forEach { challenge ->
                challenge.hashtags = hashtags[challenge.id] ?: emptyList()
                challenge.memberImages = memberImages[challenge.id] ?: emptyList()
            }
        }
        return challenges
    }

    override fun findChallengeIntroById(id: Long): FindChallengeIntroDto? {
        return queryFactory
            .from(challenge)
            .join(challengeRule).on(challenge.id.eq(challengeRule.challenge.id))
            .where(challenge.id.eq(id))
            .transform(
                groupBy(challenge.id).list(
                    QFindChallengeIntroDto(
                        list(challengeRule.rule),
                        challenge.proveTime,
                        challenge.goal,
                        challenge.startDate,
                        challenge.endDate,
                    )
                )
            )
            .firstOrNull()
    }

    override fun findChallenges(pageable: Pageable): SliceDto<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val challenges = queryFactory
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
            .where(challenge.status.eq(ACTIVE))
            .orderBy(challenge.endDate.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val challengeIds = challenges.map { it.id }
        if (challengeIds.isNotEmpty()) {
            val hashtags = getHashtags(challengeIds)
            challenges.forEach {
                it.hashtags = hashtags[it.id] ?: emptyList()
            }
        }
        return SliceImpl(challenges, pageable, hasNext(challenges, pageSize)).toSliceDto()
    }

    override fun findChallengeById(id: Long): FindChallengeDto? {
        val challenge = queryFactory
            .from(challenge)
            .join(challengeHistory).on(challenge.id.eq(challengeHistory.challengeId))
            .leftJoin(challengeRule).on(challenge.id.eq(challengeRule.challenge.id))
            .leftJoin(challengeHashtag).on(challenge.id.eq(challengeHashtag.challenge.id))
            .where(challenge.id.eq(id))
            .transform(
                groupBy(challenge.id).list(
                    QFindChallengeDto(
                        challenge.name,
                        challenge.goal,
                        challenge.imageUrl,
                        challengeHistory.challengeMemberCount,
                        challenge.isPublic,
                        challenge.proveTime,
                        challenge.endDate,
                        list(challengeRule.rule),
                        list(challengeHashtag.hashtag),
                        Expressions.constant(emptyList()),
                        Expressions.constant(""),
                    )
                )
            )
            .firstOrNull() ?: return null
        val memberImages = queryFactory
            .select(user.imageUrl)
            .from(challengeMember)
            .join(user).on(user.id.eq(challengeMember.userId))
            .where(
                challengeMember.challengeId.eq(id),
                challengeMember.status.eq(PROGRESS),
            )
            .orderBy(challengeMember.createdDateTime.desc())
            .limit(3)
            .fetch()
        val creator = queryFactory
            .select(user.username)
            .from(challengeMember)
            .join(user).on(user.id.eq(challengeMember.userId))
            .where(
                challengeMember.challengeId.eq(id),
                challengeMember.isCreator.isTrue,
                challengeMember.status.eq(PROGRESS),
            )
            .fetchOne() ?: ""
        return challenge.copy(memberImages = memberImages, creator = creator)
    }

    override fun findChallengesByHashtags(
        popularHashtags: Set<String>,
        pageable: Pageable,
    ): SliceDto<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .from(challenge)
            .join(challengeHashtag).on(challenge.id.eq(challengeHashtag.challenge.id))
            .join(challengeHistory).on(challenge.id.eq(challengeHistory.challengeId))
            .where(
                challenge.status.eq(ACTIVE),
                challengeHashtag.hashtag.`in`(popularHashtags),
            )
            .orderBy(challengeHistory.challengeMemberCount.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .transform(
                groupBy(challenge.id).list(
                    QFindChallengesDto(
                        challenge.id,
                        challenge.name,
                        challenge.endDate,
                        challenge.imageUrl,
                        list(challengeHashtag.hashtag),
                    )
                )
            )
        return SliceImpl(content, pageable, hasNext(content, pageSize)).toSliceDto()
    }

    override fun findChallengesBySpecificHashtag(
        hashtag: String,
        pageable: Pageable,
    ): SliceDto<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .from(challenge)
            .join(challengeHashtag).on(challenge.id.eq(challengeHashtag.challenge.id))
            .join(challengeHistory).on(challenge.id.eq(challengeHistory.challengeId))
            .where(
                challenge.status.eq(ACTIVE),
                challengeHashtag.hashtag.eq(hashtag),
            )
            .orderBy(challengeHistory.challengeMemberCount.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .transform(
                groupBy(challenge.id).list(
                    QFindChallengesDto(
                        challenge.id,
                        challenge.name,
                        challenge.endDate,
                        challenge.imageUrl,
                        list(challengeHashtag.hashtag),
                    )
                )
            )
        return SliceImpl(content, pageable, hasNext(content, pageSize)).toSliceDto()
    }

    override fun findChallengesByName(
        name: String,
        pageable: Pageable,
    ): SliceDto<FindChallengesByNameDto> {
        val pageSize = pageable.pageSize
        val challenges = queryFactory
            .select(
                QFindChallengesByNameDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challengeHistory.challengeMemberCount,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .join(challengeHistory).on(challenge.id.eq(challengeHistory.challengeId))
            .where(
                challenge.status.eq(ACTIVE),
                challenge.name.likeIgnoreCase("%$name"),
            )
            .orderBy(
                similarityOrder(challenge.name, name),
                challenge.endDate.desc(),
            )
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val challengeIds = challenges.map { it.id }
        if (challengeIds.isNotEmpty()) {
            val memberImages = getMemberImages(challengeIds)
            challenges.forEach { challenge ->
                challenge.memberImages = memberImages[challenge.id] ?: emptyList()
            }
        }
        return SliceImpl(challenges, pageable, hasNext(challenges, pageSize)).toSliceDto()
    }

    override fun findChallengesByHashtag(
        hashtag: String,
        pageable: Pageable,
    ): SliceDto<FindChallengesByHashtagDto> {
        val pageSize = pageable.pageSize
        val challenges = queryFactory
            .select(
                QFindChallengesByHashtagDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challengeHistory.challengeMemberCount,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                    Expressions.constant(emptyList()),
                )
            )
            .from(challenge)
            .join(challengeHistory).on(challenge.id.eq(challengeHistory.challengeId))
            .join(challengeHashtag).on(challenge.id.eq(challengeHashtag.challenge.id))
            .where(
                challenge.status.eq(ACTIVE),
                challengeHashtag.hashtag.likeIgnoreCase("%$hashtag%"),
            )
            .orderBy(
                similarityOrder(challengeHashtag.hashtag, hashtag),
                challenge.endDate.desc(),
            )
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val challengeIds = challenges.map { it.id }
        val hashtags = getHashtags(challengeIds)
        val memberImages = getMemberImages(challengeIds)
        challenges.forEach { challenge ->
            challenge.hashtags = hashtags[challenge.id] ?: emptyList()
            challenge.memberImages = memberImages[challenge.id] ?: emptyList()
        }
        return SliceImpl(challenges, pageable, hasNext(challenges, pageSize)).toSliceDto()
    }

    private fun getHashtags(challengeIds: List<Long>): Map<Long, List<String>> {
        return queryFactory
            .select(
                QFindChallengeHashtagDto(
                    challengeHashtag.challenge.id,
                    challengeHashtag.hashtag,
                )
            )
            .from(challengeHashtag)
            .where(challengeHashtag.challenge.id.`in`(challengeIds))
            .fetch()
            .groupBy { it.challengeId }
            .mapValues { it.value.map { it.hashtag } }
    }

    private fun getMemberImages(challengeIds: List<Long>): Map<Long, List<String>> {
        return queryFactory
            .select(
                QMemberImageDto(
                    challengeMember.challengeId,
                    user.imageUrl,
                )
            )
            .from(challengeMember)
            .join(user).on(challengeMember.userId.eq(user.id))
            .where(
                challengeMember.challengeId.`in`(challengeIds),
                challengeMember.status.eq(PROGRESS),
            )
            .orderBy(challengeMember.createdDateTime.desc())
            .fetch()
            .groupBy { it.challengeId }
            .mapValues { it.value.take(3).map { it.imageUrl } }
    }

    private fun <T> hasNext(content: MutableList<T>, pageSize: Int) =
        if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

    private fun similarityOrder(path: StringPath, keyword: String) =
        Expressions.numberTemplate(Int::class.java, NUMBER_TEMPLATE, path, keyword).desc()

    companion object {
        private const val NUMBER_TEMPLATE = "similarity({0}, {1})"
    }
}
