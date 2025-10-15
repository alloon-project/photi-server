package com.photi.core.domain.feed.port

interface FeedChallengeMemberPort {

    fun getChallengeMemberIdBy(userId: Long, challengeId: Long): Long
}
