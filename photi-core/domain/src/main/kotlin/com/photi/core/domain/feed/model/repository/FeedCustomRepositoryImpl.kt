package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.model.QChallenge.challenge
import com.photi.core.domain.challengemember.model.QChallengeMember.challengeMember
import com.photi.core.domain.challengemember.model.StatusType
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.toSliceDto
import com.photi.core.domain.feed.dto.FeedDto
import com.photi.core.domain.feed.dto.FindFeedDto
import com.photi.core.domain.feed.dto.QFeedDto
import com.photi.core.domain.feed.dto.QFindFeedDto
import com.photi.core.domain.feed.model.QFeed.feed
import com.photi.core.domain.feed.model.SortType
import com.photi.core.domain.feed.model.SortType.LATEST
import com.photi.core.domain.feed.model.SortType.POPULAR
import com.photi.core.domain.feedhistory.model.QFeedHistory.feedHistory
import com.photi.core.domain.feedlike.model.QFeedLike.feedLike
import com.photi.core.domain.user.model.QUser.user
import com.querydsl.core.types.Order.DESC
import com.querydsl.core.types.OrderSpecifier
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

    override fun findFeedById(userId: Long, challengeId: Long, feedId: Long): FindFeedDto? {
        return queryFactory
            .select(
                QFindFeedDto(
                    user.username,
                    user.imageUrl,
                    feed.imageUrl,
                    feed.createdDateTime,
                    feedHistory.likeCount,
                    isLike(),
                )
            )
            .from(feed)
            .join(user).on(user.id.eq(feed.userId))
            .join(feedHistory).on(feedHistory.feedId.eq(feed.id))
            .join(challengeMember)
            .on(
                challengeMember.userId.eq(userId),
                challengeMember.challengeId.eq(challengeId),
            )
            .leftJoin(feedLike)
            .on(
                feedLike.feedId.eq(feed.id),
                feedLike.challengeMemberId.eq(challengeMember.id),
            )
            .where(
                feed.id.eq(feedId),
                feed.challengeId.eq(challengeId),
            )
            .fetchFirst()
    }

    override fun findFeedsById(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortType,
    ): SliceDto<Triple<LocalDate, List<FeedDto>, Int>> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFeedDto(
                    feed.id,
                    user.username,
                    feed.imageUrl,
                    feed.createdDateTime,
                    challenge.proveTime,
                    isLike(),
                )
            )
            .from(feed)
            .join(user).on(user.id.eq(feed.userId))
            .join(challenge).on(challenge.id.eq(feed.challengeId))
            .join(challengeMember)
            .on(
                challengeMember.userId.eq(userId),
                challengeMember.challengeId.eq(challengeId),
                challengeMember.status.eq(StatusType.PROGRESS),
            )
            .join(feedHistory).on(feedHistory.feedId.eq(feed.id))
            .leftJoin(feedLike)
            .on(
                feedLike.feedId.eq(feed.id),
                feedLike.challengeMemberId.eq(challengeMember.id),
            )
            .where(feed.challengeId.eq(challengeId))
            .orderBy(*getOrderSpecifier(sort))
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        val feeds = queryFactory
            .select(feed.createdDateTime)
            .from(feed)
            .where(feed.challengeId.eq(challengeId))
            .fetch()
        val feedsCountByDate = feeds.map { it.toLocalDate() }
            .groupingBy { it }
            .eachCount()
        val groupedContent = mutableListOf<Triple<LocalDate, List<FeedDto>, Int>>()
        content.groupBy { it.createdDateTime.toLocalDate() }
            .forEach { (date, feeds) ->
                val feedMemberCnt = feedsCountByDate[date] ?: 0
                groupedContent.add(Triple(date, feeds, feedMemberCnt))
            }
        return SliceImpl(groupedContent, pageable, hasNext(content, pageSize)).toSliceDto()
    }

    override fun findFeedsByIdV2(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortType,
    ): SliceDto<FeedDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFeedDto(
                    feed.id,
                    user.username,
                    feed.imageUrl,
                    feed.createdDateTime,
                    challenge.proveTime,
                    isLike(),
                )
            )
            .from(feed)
            .join(challenge).on(challenge.id.eq(feed.challengeId))
            .join(user).on(user.id.eq(feed.userId))
            .join(challengeMember)
            .on(
                challengeMember.userId.eq(userId),
                challengeMember.challengeId.eq(challengeId),
                challengeMember.status.eq(StatusType.PROGRESS),
            )
            .join(feedHistory).on(feedHistory.feedId.eq(feed.id))
            .leftJoin(feedLike)
            .on(
                feedLike.feedId.eq(feed.id),
                feedLike.challengeMemberId.eq(challengeMember.id),
            )
            .where(feed.challengeId.eq(challengeId))
            .orderBy(*getOrderSpecifier(sort))
            .offset(pageable.offset)
            .limit(pageSize + 1L)
            .fetch()
        return SliceImpl(content, pageable, hasNext(content, pageSize)).toSliceDto()
    }

    private fun isLike() = cases()
        .`when`(feedLike.id.isNotNull).then(true)
        .otherwise(false)

    private fun getOrderSpecifier(sort: SortType) = when (sort) {
        LATEST -> getLatestSort()
        POPULAR -> getPopularSort()
    }

    private fun getLatestSort() = arrayOf(OrderSpecifier(DESC, feed.createdDateTime))

    private fun getPopularSort() = arrayOf(
        OrderSpecifier(DESC, getDateTemplate()),
        OrderSpecifier(DESC, feedHistory.likeCount.add(feedHistory.commentCount)),
    )

    private fun getDateTemplate() = Expressions.dateTemplate(
        LocalDate::class.java,
        DATE_TEMPLATE,
        feed.createdDateTime,
    )

    private fun <T> hasNext(content: MutableList<T>, pageSize: Int) =
        if (content.size > pageSize) {
            content.removeAt(pageSize)
            true
        } else {
            false
        }

    companion object {
        private const val DATE_TEMPLATE = "CAST({0} AS DATE)"
    }
}
