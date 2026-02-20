package com.photi.core.domain.feedlike.service

import com.photi.core.domain.feedlike.exception.FeedLikeException
import com.photi.core.domain.feedlike.port.FeedLikeChallengeMemberPort
import com.photi.core.domain.feedlike.port.FeedLikeFeedHistoryPort
import com.photi.core.domain.feedlike.service.command.FeedLikeCommandService
import com.photi.core.domain.feedlike.service.query.FeedLikeQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedLikeService(
    private val feedLikeQueryService: FeedLikeQueryService,
    private val feedLikeCommandService: FeedLikeCommandService,
    private val challengeMemberPort: FeedLikeChallengeMemberPort,
    private val feedHistoryPort: FeedLikeFeedHistoryPort,
    private val idempotencyKeyService: IdempotencyKeyService,
) {

    @Transactional
    fun feedLike(userId: Long, challengeId: Long, feedId: Long) {
        val challengeMemberId = getChallengeMemberIdBy(userId, challengeId)
        idempotencyKeyService.validateFeedLike(userId, feedId)
        // todo 동시성 제어 aop
        try {
            feedLikeCommandService.createFeedLike(challengeMemberId, feedId)
            feedHistoryPort.increaseLike(feedId)
        } catch (e: Exception) {
            throw FeedLikeException.ExistsFeedLikeException()
        }
    }

    @Transactional
    fun cancelFeedLike(userId: Long, challengeId: Long, feedId: Long) {
        // todo 동시성 제어 aop
        val challengeMemberId = getChallengeMemberIdBy(userId, challengeId)
        val feedLike = feedLikeQueryService.getFeedLikeBy(feedId, challengeMemberId)
            ?: throw FeedLikeException.NotFoundFeedLikeException()
        feedLikeCommandService.deleteFeedLike(feedLike)
        feedHistoryPort.decreaseLike(feedId)
    }

    private fun getChallengeMemberIdBy(userId: Long, challengeId: Long) =
        challengeMemberPort.getChallengeMemberIdBy(userId, challengeId)
}
