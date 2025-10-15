package com.photi.core.domain.feedcomment.service

import com.photi.core.domain.feedcomment.dto.RegisterFeedCommentDto
import com.photi.core.domain.feedcomment.dto.RegisterFeedCommentRequestDto
import com.photi.core.domain.feedcomment.exception.FeedCommentException
import com.photi.core.domain.feedcomment.port.FeedCommentChallengeMemberPort
import com.photi.core.domain.feedcomment.port.FeedCommentFeedHistoryPort
import com.photi.core.domain.feedcomment.service.command.FeedCommentCommandService
import com.photi.core.domain.feedcomment.service.query.FeedCommentQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedCommentService(
    private val feedCommentQueryService: FeedCommentQueryService,
    private val feedCommentCommandService: FeedCommentCommandService,
    private val challengeMemberPort: FeedCommentChallengeMemberPort,
    private val feedHistoryPort: FeedCommentFeedHistoryPort,
) {

    @Transactional
    fun registerFeedComment(
        userId: Long,
        challengeId: Long,
        feedId: Long,
        dto: RegisterFeedCommentRequestDto,
    ): RegisterFeedCommentDto {
        // todo 동시성 제어 aop
        val challengeMemberId = getChallengeMemberIdBy(userId, challengeId)
        val feedComment =
            feedCommentCommandService.createFeedComment(dto, userId, challengeMemberId, feedId)
        feedHistoryPort.increaseComment(feedId)
        return RegisterFeedCommentDto.of(feedComment)
    }

    @Transactional
    fun deleteFeedComment(userId: Long, challengeId: Long, feedId: Long, commentId: Long) {
        // todo 동시성 제어 aop
        getChallengeMemberIdBy(userId, challengeId)
        val feedComment = feedCommentQueryService.getFeedCommentBy(commentId).orElseThrow {
            throw FeedCommentException.NotFoundFeedCommentException()
        }
        feedCommentCommandService.deleteFeedComment(feedComment)
        feedHistoryPort.decreaseComment(feedId)
    }

    fun findFeedComments(feedId: Long, page: Int, size: Int) =
        feedCommentQueryService.getFeedCommentsBy(feedId, page, size)

    private fun getChallengeMemberIdBy(userId: Long, challengeId: Long) =
        challengeMemberPort.getChallengeMemberIdBy(userId, challengeId)
}
