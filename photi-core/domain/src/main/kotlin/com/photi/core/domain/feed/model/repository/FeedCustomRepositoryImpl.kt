package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeFeedDto
import com.photi.core.domain.challenge.dto.FindChallengeFeedsDto
import com.photi.core.domain.challenge.dto.QFindChallengeFeedDto
import com.photi.core.domain.challenge.dto.QFindChallengeFeedsDto
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.consts.SortTypeConstants
import com.photi.core.domain.common.model.ServiceStatus
import com.photi.core.domain.common.toSliceDto
import com.photi.core.domain.feed.model.Feed
import com.photi.core.domain.feed.model.QFeed.feed
import com.photi.core.domain.feed.model.QFeedLike.feedLike
import com.querydsl.core.types.Order.DESC
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.Expressions.cases
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class FeedCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FeedCustomRepository {

    override fun find(id: Long): Feed? {
        return queryFactory
            .selectFrom(feed)
            .where(
                feed.id.eq(id),
                eqServiceStatus(ServiceStatus.ACTIVE)
            ).fetchFirst()
    }

    override fun findByFeedId(id: Long): Feed? {
        return queryFactory
            .selectFrom(feed)
            .join(feed.challengeMember).fetchJoin()
            .where(feed.id.eq(id))
            .fetchFirst()
    }

    override fun findContentById(
        challengeId: Long,
        feedId: Long,
        userId: Long,
    ): FindChallengeFeedDto? {
        return queryFactory
            .select(
                QFindChallengeFeedDto(
                    feed.challengeMember.user.username,
                    feed.challengeMember.user.imageUrl,
                    feed.imageUrl,
                    feed.createDateTime,
                    feed.likeCnt,
                    cases()
                        .`when`(feedLike.id.isNotNull).then(true)
                        .otherwise(false),
                )
            )
            .from(feed)
            .join(feed.challengeMember)
            .join(feed.challenge)
            .leftJoin(feedLike).on(
                feedLike.feed.id.eq(feed.id),
                feedLike.challengeMember.user.id.eq(userId),
            )
            .where(feed.id.eq(feedId), feed.challenge.id.eq(challengeId))
            .fetchFirst()
    }

    override fun findAllByChallengeId(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants,
    ): SliceDto<Triple<LocalDate, List<FindChallengeFeedsDto>, Int>> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindChallengeFeedsDto(
                    feed.id,
                    feed.challengeMember.user.username,
                    feed.imageUrl,
                    feed.createDateTime,
                    feed.challenge.proveTime,
                    cases()
                        .`when`(feedLike.id.isNotNull).then(true)
                        .otherwise(false)
                )
            )
            .from(feed)
            .join(feed.challenge)
            .join(feed.challengeMember.user)
            .leftJoin(feedLike).on(
                feedLike.feed.id.eq(feed.id), feedLike.challengeMember.user.id.eq(userId)
            )
            .where(feed.challenge.id.eq(challengeId))
            .orderBy(*getOrderSpecifier(sort))
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val feeds = queryFactory
            .select(feed.createDateTime)
            .from(feed)
            .join(feed.challenge)
            .where(feed.challenge.id.eq(challengeId))
            .fetch()
        val feedsCountByDate = feeds.map { it.toLocalDate() }
            .groupingBy { it }
            .eachCount()

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        val groupedContent = mutableListOf<Triple<LocalDate, List<FindChallengeFeedsDto>, Int>>()
        content
            .groupBy { it.createdDateTime.toLocalDate() }
            .forEach { (date, feeds) ->
                val feedMemberCnt = feedsCountByDate[date] ?: 0
                groupedContent.add(Triple(date, feeds, feedMemberCnt))
            }

        return SliceImpl(groupedContent, pageable, hasNext).toSliceDto()
    }

    override fun findAllByChallengeIdV2(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants
    ): SliceDto<FindChallengeFeedsDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindChallengeFeedsDto(
                    feed.id,
                    feed.challengeMember.user.username,
                    feed.imageUrl,
                    feed.createDateTime,
                    feed.challenge.proveTime,
                    cases()
                        .`when`(feedLike.id.isNotNull).then(true)
                        .otherwise(false)
                )
            )
            .from(feed)
            .join(feed.challenge)
            .join(feed.challengeMember.user)
            .leftJoin(feedLike).on(
                feedLike.feed.id.eq(feed.id), feedLike.challengeMember.user.id.eq(userId)
            )
            .where(feed.challenge.id.eq(challengeId))
            .orderBy(*getOrderSpecifier(sort))
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()

        val hasNext = if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

        return SliceImpl(content, pageable, hasNext).toSliceDto()
    }

    override fun findFeedMemberCntByChallengeId(challengeId: Long): Long? {
        return queryFactory
            .select(feed.count())
            .from(feed)
            .where(
                feed.challenge.id.eq(challengeId),
                feed.createDateTime.year().eq(LocalDate.now().year),
                feed.createDateTime.month().eq(LocalDate.now().monthValue),
                feed.createDateTime.dayOfMonth().eq(LocalDate.now().dayOfMonth),
            )
            .fetchOne()
    }

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { feed.serviceStatus.eq(serviceStatus) }

    private fun getOrderSpecifier(sort: SortTypeConstants): Array<OrderSpecifier<*>> {
        return when (sort) {
            SortTypeConstants.LATEST -> arrayOf(OrderSpecifier(DESC, feed.createDateTime))
            SortTypeConstants.POPULAR -> arrayOf(
                OrderSpecifier(DESC, getDateTemplate()),
                OrderSpecifier(DESC, feed.likeCnt.add(feed.commentCnt)),
            )
        }
    }

    private fun getDateTemplate() =
        Expressions.dateTemplate(LocalDate::class.java, "CAST({0} AS DATE)", feed.createDateTime)
}
