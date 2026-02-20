package com.photi.core.domain.feedlike.port

interface FeedLikeChallengeMemberPort {

    fun getChallengeMemberIdBy(userId: Long, challengeId: Long): Long
}
