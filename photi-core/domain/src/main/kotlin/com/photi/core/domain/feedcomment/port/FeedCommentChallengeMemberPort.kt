package com.photi.core.domain.feedcomment.port

interface FeedCommentChallengeMemberPort {

    fun getChallengeMemberIdBy(userId: Long, challengeId: Long): Long
}
