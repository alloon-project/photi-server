package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.domain.feed.FeedComment
import com.alloon.alloonserver.domain.feed.QFeedComment.feedComment
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FeedCommentCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : FeedCommentCustomRepository {

    override fun findByCommentId(commentId: Long): FeedComment? {
        return queryFactory
            .selectFrom(feedComment)
            .join(feedComment.feed).fetchJoin()
            .join(feedComment.challengeMember).fetchJoin()
            .where(feedComment.id.eq(commentId))
            .fetchFirst()
    }
}