package com.photi.core.domain.feedhistory.adapter

import com.photi.core.domain.feed.exception.FeedException
import com.photi.core.domain.feedcomment.port.FeedCommentFeedHistoryPort
import com.photi.core.domain.feedhistory.service.query.FeedHistoryQueryService
import com.photi.core.domain.feedlike.port.FeedLikeFeedHistoryPort
import org.springframework.stereotype.Component

@Component
class FeedHistoryAdapter(
    private val feedHistoryQueryService: FeedHistoryQueryService,
) : FeedLikeFeedHistoryPort, FeedCommentFeedHistoryPort {

    override fun increaseLike(feedId: Long) {
        getFeedHistoryBy(feedId).increaseLike()
    }

    override fun decreaseLike(feedId: Long) {
        getFeedHistoryBy(feedId).decreaseLike()
    }

    override fun increaseComment(feedId: Long) {
        getFeedHistoryBy(feedId).increaseComment()
    }

    override fun decreaseComment(feedId: Long) {
        getFeedHistoryBy(feedId).decreaseComment()
    }

    private fun getFeedHistoryBy(feedId: Long) = feedHistoryQueryService.getFeedHistoryBy(feedId)
        ?: throw FeedException.NotFoundFeedException()
}
