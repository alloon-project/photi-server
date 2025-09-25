package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeFeedCommentsDto
import com.photi.core.domain.challenge.dto.QFindChallengeFeedCommentsDto
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.toSliceDto
import com.photi.core.domain.feed.model.FeedComment
import com.photi.core.domain.feed.model.QFeedComment.feedComment
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class FeedCommentCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
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
    ): SliceDto<FindChallengeFeedCommentsDto> {
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
            .orderBy(feedComment.createDateTime.desc())
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
}
