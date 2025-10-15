package com.photi.core.domain.feedlike.service.query

import com.photi.core.domain.feedlike.model.repository.FeedLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedLikeQueryService(
    private val feedLikeRepository: FeedLikeRepository,
) {

    fun getFeedLikeBy(feedId: Long, challengeMemberId: Long) =
        feedLikeRepository.findByFeedIdAndChallengeMemberId(feedId, challengeMemberId)
}
