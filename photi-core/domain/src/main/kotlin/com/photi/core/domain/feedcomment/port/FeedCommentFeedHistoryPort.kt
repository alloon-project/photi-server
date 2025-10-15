package com.photi.core.domain.feedcomment.port

interface FeedCommentFeedHistoryPort {

    fun increaseComment(feedId: Long)

    fun decreaseComment(feedId: Long)
}
