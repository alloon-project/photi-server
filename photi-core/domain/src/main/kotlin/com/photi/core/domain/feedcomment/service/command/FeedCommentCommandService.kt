package com.photi.core.domain.feedcomment.service.command

import com.photi.core.domain.feedcomment.dto.RegisterFeedCommentRequestDto
import com.photi.core.domain.feedcomment.model.FeedComment
import com.photi.core.domain.feedcomment.model.repository.FeedCommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedCommentCommandService(
    private val feedCommentRepository: FeedCommentRepository,
) {

    fun createFeedComment(
        dto: RegisterFeedCommentRequestDto,
        userId: Long,
        challengeMemberId: Long,
        feedId: Long,
    ) = feedCommentRepository.save(dto.toEntity(userId, challengeMemberId, feedId))

    fun deleteFeedComment(feedComment: FeedComment) {
        feedCommentRepository.delete(feedComment)
    }

    fun deleteFeedComments(feedId: Long) {
        feedCommentRepository.deleteByFeedId(feedId)
    }
}
