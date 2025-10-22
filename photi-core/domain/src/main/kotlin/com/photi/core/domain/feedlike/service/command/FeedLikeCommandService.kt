package com.photi.core.domain.feedlike.service.command

import com.photi.core.domain.feedlike.model.FeedLike
import com.photi.core.domain.feedlike.model.repository.FeedLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedLikeCommandService(
    private val feedLikeRepository: FeedLikeRepository,
) {

    fun createFeedLike(challengeMemberId: Long, feedId: Long) {
        feedLikeRepository.save(FeedLike(challengeMemberId, feedId))
    }

    fun deleteFeedLike(feedLike: FeedLike) {
        feedLikeRepository.delete(feedLike)
    }

    fun deleteFeedLikes(feedId: Long) {
        feedLikeRepository.deleteByFeedId(feedId)
    }
}
