package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.QFeed.feed
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

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

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { feed.serviceStatus.eq(serviceStatus) }
}