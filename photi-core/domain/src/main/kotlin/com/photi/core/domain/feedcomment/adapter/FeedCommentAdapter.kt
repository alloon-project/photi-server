package com.photi.core.domain.feedcomment.adapter

import com.photi.core.domain.feed.port.FeedFeedCommentPort
import com.photi.core.domain.feedcomment.service.command.FeedCommentCommandService
import org.springframework.stereotype.Component

@Component
class FeedCommentAdapter(
    private val feedCommentCommandService: FeedCommentCommandService,
) : FeedFeedCommentPort {

    override fun deleteFeedComments(feedId: Long) {
        feedCommentCommandService.deleteFeedComments(feedId)
    }
}
