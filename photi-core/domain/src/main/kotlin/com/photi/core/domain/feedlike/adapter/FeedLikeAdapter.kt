package com.photi.core.domain.feedlike.adapter

import com.photi.core.domain.feed.port.FeedFeedLikePort
import com.photi.core.domain.feedlike.service.command.FeedLikeCommandService
import org.springframework.stereotype.Component

@Component
class FeedLikeAdapter(
    private val feedLikeCommandService: FeedLikeCommandService,
) : FeedFeedLikePort {

    override fun deleteFeedLikes(feedId: Long) {
        feedLikeCommandService.deleteFeedLikes(feedId)
    }
}
