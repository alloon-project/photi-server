package com.photi.server.domain.user.custom

import com.photi.server.domain.base.ServiceStatus.END
import com.photi.server.domain.challenge.ChallengeMemberStatus
import com.photi.server.domain.challenge.ChallengeMemberStatus.PROGRESS
import com.photi.server.domain.challenge.QChallengeHashtag.challengeHashtag
import com.photi.server.domain.challenge.QChallengeMember.challengeMember
import com.photi.server.domain.feed.QFeed.feed
import com.photi.server.domain.user.QContact.contact
import com.photi.server.domain.user.QUser.user
import com.photi.server.domain.user.User
import com.photi.server.service.challenge.dto.QFindChallengeHashtagDto
import com.photi.server.service.challenge.dto.QUserImageDto
import com.photi.server.service.user.dto.*
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class UserCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserCustomRepository {

    override fun findFetchContact(email: String?, username: String?, userId: Long?): User? {
        return queryFactory
            .selectFrom(user)
            .innerJoin(user.contact, contact)
            .where(
                eqUserId(userId),
                eqUsername(username),
                eqEmail(email),
                contact.verifyYn.isTrue
            ).fetchOne()
    }

    override fun find(userId: Long): User? {
        return queryFactory
            .selectFrom(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findInfoById(userId: Long): UserInfoDto? {
        return queryFactory
            .select(
                QUserInfoDto(
                    user.imageUrl,
                    user.username,
                    user.contact.email
                )
            )
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findChallengeHistoryById(userId: Long): UserChallengeHistoryDto? {
        val endedChallengeCnt = queryFactory
            .select(challengeMember.count())
            .from(challengeMember)
            .join(challengeMember.user)
            .join(challengeMember.challenge)
            .where(
                challengeMember.user.id.eq(userId)
                    .and(challengeMember.challenge.serviceStatus.eq(END))
            )
            .fetchOne()?.toInt() ?: 0

        return queryFactory
            .select(
                QUserChallengeHistoryDto(
                    user.username,
                    user.imageUrl,
                    user.feedCnt,
                    Expressions.constant(endedChallengeCnt),
                )
            )
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findFeedsById(userId: Long): List<String>? {
        val feeds = queryFactory
            .select(feed.createDateTime)
            .from(feed)
            .join(feed.challengeMember.user)
            .where(feed.challengeMember.user.id.eq(userId))
            .fetch()
            .map { it.toLocalDate().toString() }

        return queryFactory
            .select(Expressions.constant(feeds))
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findChallengeCntById(userId: Long): FindUserChallengeCntDto? {
        val challengeCnt = queryFactory
            .select(challengeMember.count())
            .from(challengeMember)
            .join(challengeMember.user)
            .where(challengeMember.user.id.eq(userId), eqChallengeMemberStatus(PROGRESS))
            .fetchOne()?.toInt() ?: 0

        return queryFactory
            .select(QFindUserChallengeCntDto(user.username, Expressions.constant(challengeCnt)))
            .from(user)
            .where(user.id.eq(userId))
            .fetchOne()
    }

    override fun findFeedsByDate(userId: Long, date: LocalDate): List<FindUserFeedsByDateDto> {
        return queryFactory
            .select(
                QFindUserFeedsByDateDto(
                    feed.id,
                    feed.challenge.id,
                    feed.imageUrl,
                    feed.challenge.name,
                    feed.challenge.proveTime
                )
            )
            .from(feed)
            .join(feed.challenge)
            .join(feed.challengeMember.user)
            .where(
                feed.challengeMember.user.id.eq(userId),
                feed.createDateTime.year().eq(date.year),
                feed.createDateTime.month().eq(date.monthValue),
                feed.createDateTime.dayOfMonth().eq(date.dayOfMonth)
            )
            .orderBy(feed.challenge.proveTime.asc())
            .fetch()
    }

    override fun findFeedHistoryById(
        userId: Long,
        pageable: Pageable
    ): Slice<FindUserFeedHistoryDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindUserFeedHistoryDto(
                    feed.id,
                    feed.challenge.id,
                    feed.imageUrl,
                    feed.createDateTime,
                    feed.challenge.name,
                    feed.challenge.invitationCode,
                )
            )
            .from(feed)
            .join(feed.challenge)
            .join(feed.challengeMember.user)
            .where(feed.challengeMember.user.id.eq(userId))
            .orderBy(feed.createDateTime.desc())
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

    override fun findEndedChallengesById(
        userId: Long,
        pageable: Pageable
    ): Slice<FindUserEndedChallengesDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindUserEndedChallengesDto(
                    challengeMember.challenge.id,
                    challengeMember.challenge.name,
                    challengeMember.challenge.imageUrl,
                    challengeMember.challenge.endDate,
                    challengeMember.challenge.currentMemberCnt,
                    Expressions.constant(emptyList())
                )
            )
            .from(challengeMember)
            .join(challengeMember.user)
            .join(challengeMember.challenge)
            .where(
                challengeMember.user.id.eq(userId),
                challengeMember.challenge.serviceStatus.eq(END)
            )
            .orderBy(challengeMember.challenge.endDate.desc())
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

    override fun findUserChallengesById(
        userId: Long,
        pageable: Pageable
    ): Slice<FindUserChallengesDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindUserChallengesDto(
                    challengeMember.challenge.id,
                    challengeMember.challenge.name,
                    challengeMember.challenge.imageUrl,
                    challengeMember.challenge.proveTime,
                    challengeMember.challenge.endDate,
                    Expressions.constant(emptyList()),
                    Expressions.constant(""),
                    Expressions.constant(0L),
                    Expressions.constant(false),
                )
            )
            .from(challengeMember)
            .join(challengeMember.challenge)
            .join(challengeMember.user)
            .where(challengeMember.user.id.eq(userId), eqChallengeMemberStatus(PROGRESS))
            .orderBy(challengeMember.challenge.proveTime.asc())
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        val challengeIds = content.map { it.id }
        val now = LocalDate.now()
        val feedImageUrls = queryFactory
            .select(QUserImageDto(feed.challenge.id, feed.imageUrl, feed.id))
            .from(feed)
            .join(feed.challengeMember)
            .where(
                feed.challengeMember.user.id.eq(userId),
                feed.challenge.id.`in`(challengeIds),
                feed.createDateTime.year().eq(now.year),
                feed.createDateTime.month().eq(now.monthValue),
                feed.createDateTime.dayOfMonth().eq(now.dayOfMonth),
            )
            .fetch()
            .groupBy { it.challengeId }
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
            val feedImageUrl = feedImageUrls[it.id]?.first()
            val imageUrl = feedImageUrl?.imageUrl ?: ""
            it.feedImageUrl = imageUrl
            it.feedId = feedImageUrl?.feedId
            it.isProve = imageUrl.isNotEmpty()
        }

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        return SliceImpl(content, pageable, hasNext)
    }

    override fun findIsProveByChallengeId(
        userId: Long,
        challengeId: Long
    ): Boolean {
        val now = LocalDate.now()
        return queryFactory.select(feed.isNotNull)
            .from(feed)
            .where(
                feed.challengeMember.user.id.eq(userId),
                feed.challenge.id.eq(challengeId),
                feed.createDateTime.year().eq(now.year),
                feed.createDateTime.month().eq(now.monthValue),
                feed.createDateTime.dayOfMonth().eq(now.dayOfMonth),
            )
            .fetchOne() ?: false
    }

    private fun eqUserId(userId: Long?): BooleanExpression? = userId?.let { user.id.eq(it) }

    private fun eqEmail(email: String?): BooleanExpression? = email?.let { contact.email.eq(it) }

    private fun eqUsername(username: String?): BooleanExpression? =
        username?.let { user.username.eq(it) }

    private fun eqChallengeMemberStatus(status: ChallengeMemberStatus): BooleanExpression =
        challengeMember.status.eq(status)
}