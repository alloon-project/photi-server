package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.domain.feed.FeedComment
import com.alloon.alloonserver.domain.feed.QFeedComment.feedComment
import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedCommentsDto
import com.alloon.alloonserver.service.challenge.dto.QFindChallengeFeedCommentsDto
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
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

    override fun findAllByFeedId(
        feedId: Long,
        pageable: Pageable
    ): Slice<FindChallengeFeedCommentsDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindChallengeFeedCommentsDto(
                    feedComment.id,
                    feedComment.challengeMember.user.username,
                    feedComment.comment,
                )
            )
            .from(feedComment)
            .join(feedComment.feed)
            .join(feedComment.challengeMember.user)
            .where(feedComment.feed.id.eq(feedId))
            .orderBy(feedComment.createDateTime.asc())
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
}