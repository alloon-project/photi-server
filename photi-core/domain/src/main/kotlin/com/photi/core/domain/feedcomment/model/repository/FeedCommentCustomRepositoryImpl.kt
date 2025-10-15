package com.photi.core.domain.feedcomment.model.repository

import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.toSliceDto
import com.photi.core.domain.feedcomment.dto.FindFeedCommentsDto
import com.photi.core.domain.feedcomment.dto.QFindFeedCommentsDto
import com.photi.core.domain.feedcomment.model.QFeedComment.feedComment
import com.photi.core.domain.user.model.QUser.user
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
class FeedCommentCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FeedCommentCustomRepository {

    override fun findFeedCommentsById(
        feedId: Long,
        pageable: Pageable,
    ): SliceDto<FindFeedCommentsDto> {
        val pageSize = pageable.pageSize
        val content = queryFactory
            .select(
                QFindFeedCommentsDto(
                    feedComment.id,
                    user.username,
                    feedComment.comment,
                )
            )
            .from(feedComment)
            .join(user).on(user.id.eq(feedComment.userId))
            .where(feedComment.feedId.eq(feedId))
            .orderBy(feedComment.createdDateTime.desc())
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
