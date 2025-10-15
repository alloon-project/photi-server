package com.photi.core.domain.feedlike.model.repository

import com.photi.core.domain.feedlike.model.FeedLike
import org.springframework.data.jpa.repository.JpaRepository

interface FeedLikeRepository : JpaRepository<FeedLike, Long> {

    fun findByFeedIdAndChallengeMemberId(feedId: Long, challengeMemberId: Long): FeedLike?
}
