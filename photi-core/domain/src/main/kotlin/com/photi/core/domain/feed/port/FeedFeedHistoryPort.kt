package com.photi.core.domain.feed.port

interface FeedFeedHistoryPort {

    fun createFeedHistory(feedId: Long)

    fun deleteFeedHistory(feedId: Long)
}
