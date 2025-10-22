package com.photi.core.domain.feedlike.port

interface FeedLikeFeedHistoryPort {

    fun increaseLike(feedId: Long)

    fun decreaseLike(feedId: Long)
}
