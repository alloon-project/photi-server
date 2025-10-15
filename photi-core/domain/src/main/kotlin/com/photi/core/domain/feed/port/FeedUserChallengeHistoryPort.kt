package com.photi.core.domain.feed.port

interface FeedUserChallengeHistoryPort {

    fun increaseFeed(userId: Long)

    fun decreaseFeed(userId: Long)
}
