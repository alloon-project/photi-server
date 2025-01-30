package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.common.constant.SortTypeConstants
import com.alloon.alloonserver.common.constant.SortTypeConstants.LATEST
import com.alloon.alloonserver.common.constant.SortTypeConstants.POPULAR
import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.QFeed.feed
import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedDto
import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedsDto
import com.alloon.alloonserver.service.challenge.dto.QFindChallengeFeedDto
import com.alloon.alloonserver.service.challenge.dto.QFindChallengeFeedsDto
import com.querydsl.core.types.Order.DESC
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
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
                eqServiceStatus(ACTIVE)
            ).fetchFirst()
    }

    override fun findByFeedId(id: Long): Feed? {
        return queryFactory
            .selectFrom(feed)
            .join(feed.challengeMember).fetchJoin()
            .where(feed.id.eq(id))
            .fetchFirst()
    }

    override fun findContentById(challengeId: Long, id: Long): FindChallengeFeedDto? {
        return queryFactory
            .select(
                QFindChallengeFeedDto(
                    feed.challengeMember.user.username,
                    feed.challengeMember.user.imageUrl,
                    feed.imageUrl,
                    feed.createDateTime,
                    feed.likeCnt,
                )
            )
            .from(feed)
            .join(feed.challengeMember)
            .join(feed.challenge)
            .where(feed.id.eq(id), feed.challenge.id.eq(challengeId))
            .fetchFirst()
    }

    override fun findAllByChallengeId(
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants
    ): Slice<Triple<LocalDate, List<FindChallengeFeedsDto>, Int>> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindChallengeFeedsDto(
                    feed.id,
                    feed.challengeMember.user.username,
                    feed.imageUrl,
                    feed.createDateTime,
                    feed.challenge.proveTime,
                )
            )
            .from(feed)
            .join(feed.challenge)
            .join(feed.challengeMember.user)
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

        return SliceImpl(groupedContent, pageable, hasNext)
    }

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { feed.serviceStatus.eq(serviceStatus) }

    private fun getOrderSpecifier(sort: SortTypeConstants): Array<OrderSpecifier<*>> {
        return when (sort) {
            LATEST -> arrayOf(OrderSpecifier(DESC, feed.createDateTime))
            POPULAR -> arrayOf(
                OrderSpecifier(DESC, feed.likeCnt.add(feed.commentCnt)),
                OrderSpecifier(DESC, feed.createDateTime),
            )
        }
    }
}