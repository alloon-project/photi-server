package com.photi.core.domain.feedcomment.service.query

import com.photi.core.domain.feedcomment.model.repository.FeedCommentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedCommentQueryService(
    private val feedCommentRepository: FeedCommentRepository,
) {

    fun getFeedCommentBy(commentId: Long, userId: Long, challengeMemberId: Long) =
        feedCommentRepository.findByIdAndUserIdAndChallengeMemberId(
            commentId,
            userId,
            challengeMemberId,
        )

    fun getFeedCommentsBy(feedId: Long, page: Int, size: Int) =
        feedCommentRepository.findFeedCommentsById(feedId, PageRequest.of(page, size))
}
