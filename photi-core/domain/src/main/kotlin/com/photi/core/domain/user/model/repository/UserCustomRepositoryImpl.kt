package com.photi.core.domain.user.model.repository

import com.photi.core.domain.challenge.dto.QFindChallengeHashtagDto
import com.photi.core.domain.challenge.model.QChallenge.challenge
import com.photi.core.domain.challenge.model.QChallengeHashtag.challengeHashtag
import com.photi.core.domain.challengehistory.model.QChallengeHistory.challengeHistory
import com.photi.core.domain.challengemember.model.QChallengeMember.challengeMember
import com.photi.core.domain.challengemember.model.StatusType.PROGRESS
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.challenge.model.StatusType.ACTIVE
import com.photi.core.domain.challenge.model.StatusType.END
import com.photi.core.domain.common.toSliceDto
import com.photi.core.domain.feed.model.QFeed.feed
import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.model.QUser.user
import com.photi.core.domain.userchallengehistory.model.QUserChallengeHistory.userChallengeHistory
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class UserCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserCustomRepository {

    override fun findChallengeHistoryById(userId: Long): FindChallengeHistoryDto? {
        val endedChallengeCount = queryFactory
            .select(challengeMember.count())
            .from(challengeMember)
            .join(challenge).on(challenge.id.eq(challengeMember.challengeId))
            .where(
                challengeMember.userId.eq(userId),
                challengeMember.status.eq(PROGRESS),
                challenge.status.eq(END),
            )
            .fetchOne() ?: 0L
        return queryFactory
            .select(
                QFindChallengeHistoryDto(
                    user.username,
                    user.imageUrl,
                    userChallengeHistory.feedCount,
                    Expressions.constant(endedChallengeCount),
                    user.createdDateTime,
                )
            )
            .from(user)
            .join(userChallengeHistory).on(user.id.eq(userChallengeHistory.userId))
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findFeedDatesById(userId: Long): List<String> {
        return queryFactory
            .select(feed.createdDateTime)
            .from(user)
            .join(feed).on(user.id.eq(feed.userId))
            .where(user.id.eq(userId))
            .fetch()
            .map { it.toLocalDate().toString() }
            .distinct()
    }

    override fun findChallengeCountById(userId: Long): FindChallengeCountDto? {
        return queryFactory
            .select(
                QFindChallengeCountDto(
                    user.username,
                    userChallengeHistory.challengeCount,
                )
            )
            .from(user)
            .join(userChallengeHistory).on(user.id.eq(userChallengeHistory.userId))
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findFeedsByDate(userId: Long, date: LocalDate): List<FindFeedsByDateDto> {
        return queryFactory
            .select(
                QFindFeedsByDateDto(
                    feed.id,
                    feed.challengeId,
                    feed.imageUrl,
                    challenge.name,
                    feed.createdDateTime,
                    challengeMember.status,
                )
            )
            .from(feed)
            .join(challenge).on(feed.challengeId.eq(challenge.id))
            .join(challengeMember).on(feed.challengeMemberId.eq(challengeMember.id))
            .where(
                feed.userId.eq(userId),
                feed.createdDateTime.year().eq(date.year),
                feed.createdDateTime.month().eq(date.monthValue),
                feed.createdDateTime.dayOfMonth().eq(date.dayOfMonth),
            )
            .orderBy(feed.createdDateTime.asc())
            .fetch()
    }

    override fun findFeedHistoryById(
        userId: Long,
        pageable: Pageable,
    ): SliceDto<FindFeedHistoryDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindFeedHistoryDto(
                    feed.id,
                    feed.challengeId,
                    feed.imageUrl,
                    feed.createdDateTime,
                    challenge.name,
                    challenge.invitationCode,
                    challengeMember.status,
                )
            )
            .from(feed)
            .join(challenge).on(feed.challengeId.eq(challenge.id))
            .join(challengeMember).on(feed.challengeMemberId.eq(challengeMember.id))
            .where(feed.userId.eq(userId))
            .orderBy(feed.createdDateTime.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        return SliceImpl(content, pageable, hasNext(content, pageSize)).toSliceDto()
    }

    override fun findEndedChallengesById(
        userId: Long,
        pageable: Pageable,
    ): SliceDto<FindEndedChallengesDto> {
        val pageSize = pageable.pageSize
        val challenges = queryFactory
            .select(
                QFindEndedChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.endDate,
                    challengeHistory.challengeMemberCount,
                    Expressions.constant(emptyList()),
                )
            )
            .from(challengeMember)
            .join(challenge).on(challengeMember.challengeId.eq(challenge.id))
            .join(challengeHistory).on(challengeHistory.challengeId.eq(challenge.id))
            .where(
                challengeMember.userId.eq(userId),
                challengeMember.status.eq(PROGRESS),
                challenge.status.eq(END),
            )
            .orderBy(challenge.endDate.desc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val challengeIds = challenges.map { it.id }
        if (challengeIds.isNotEmpty()) {
            val memberImages = queryFactory
                .select(
                    QMemberImageDto(
                        challengeMember.challengeId,
                        user.imageUrl,
                    )
                )
                .from(challengeMember)
                .join(user).on(challengeMember.userId.eq(userId))
                .where(
                    challengeMember.challengeId.`in`(challengeIds),
                    challengeMember.status.eq(PROGRESS),
                )
                .orderBy(challengeMember.createdDateTime.desc())
                .fetch()
                .groupBy { it.challengeId }
                .mapValues { it.value.take(3).map { it.imageUrl } }
            challenges.forEach {
                it.memberImages = memberImages[it.id] ?: emptyList()
            }
        }
        return SliceImpl(challenges, pageable, hasNext(challenges, pageSize)).toSliceDto()
    }

    override fun findChallengesById(userId: Long, pageable: Pageable): SliceDto<FindChallengesDto> {
        val pageSize = pageable.pageSize
        val challenges = queryFactory
            .select(
                QFindChallengesDto(
                    challenge.id,
                    challenge.name,
                    challenge.imageUrl,
                    challenge.proveTime,
                    challenge.endDate,
                    Expressions.constant(emptyList()),
                    Expressions.constant(""),
                    Expressions.constant(0L),
                    Expressions.constant(false),
                )
            )
            .from(challengeMember)
            .join(challenge).on(challengeMember.challengeId.eq(challenge.id))
            .where(
                challengeMember.userId.eq(userId),
                challengeMember.status.eq(PROGRESS),
                challenge.status.eq(ACTIVE),
            )
            .orderBy(challenge.proveTime.asc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val challengeIds = challenges.map { it.id }
        if (challengeIds.isNotEmpty()) {
            val todayStart = LocalDate.now().atStartOfDay()
            val todayEnd = todayStart.plusDays(1)
            val feeds = queryFactory
                .select(
                    QFeedImageDto(
                        feed.challengeId,
                        feed.imageUrl,
                        feed.id,
                    )
                )
                .from(feed)
                .where(
                    feed.userId.eq(userId),
                    feed.challengeId.`in`(challengeIds),
                    feed.createdDateTime.between(todayStart, todayEnd),
                )
                .fetch()
                .groupBy { it.challengeId }
                .mapValues { it.value.firstOrNull() }
            val hashtags = queryFactory
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
            challenges.forEach { challenge ->
                challenge.hashtags = hashtags[challenge.id] ?: emptyList()
                feeds[challenge.id]?.let {
                    challenge.feedImageUrl = it.imageUrl
                    challenge.feedId = it.feedId
                    challenge.isProve = true
                }
            }
        }
        return SliceImpl(challenges, pageable, hasNext(challenges, pageSize)).toSliceDto()
    }

    override fun findChallengeIsProveById(userId: Long, challengeId: Long): Boolean {
        val todayStart = LocalDate.now().atStartOfDay()
        val todayEnd = todayStart.plusDays(1)
        return queryFactory
            .select(feed.isNotNull)
            .from(feed)
            .where(
                feed.userId.eq(userId),
                feed.challengeId.eq(challengeId),
                feed.createdDateTime.between(todayStart, todayEnd),
            )
            .fetchOne() ?: false
    }

    private fun <T> hasNext(content: MutableList<T>, pageSize: Int) =
        if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }
}
