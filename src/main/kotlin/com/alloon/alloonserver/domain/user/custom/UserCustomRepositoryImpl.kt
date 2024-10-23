package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.base.ServiceStatus.END
import com.alloon.alloonserver.domain.challenge.QChallengeMember.challengeMember
import com.alloon.alloonserver.domain.feed.QFeed.feed
import com.alloon.alloonserver.domain.user.QContact.contact
import com.alloon.alloonserver.domain.user.QUser.user
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.user.dto.*
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
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
            .where(challengeMember.user.id.eq(userId))
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

    private fun eqUserId(userId: Long?): BooleanExpression? = userId?.let { user.id.eq(it) }

    private fun eqEmail(email: String?): BooleanExpression? = email?.let { contact.email.eq(it) }

    private fun eqUsername(username: String?): BooleanExpression? =
        username?.let { user.username.eq(it) }
}