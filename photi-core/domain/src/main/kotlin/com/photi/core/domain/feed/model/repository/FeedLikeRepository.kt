package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.feed.model.FeedLike
import org.springframework.data.jpa.repository.JpaRepository

interface FeedLikeRepository : JpaRepository<FeedLike, Long> {

    fun findByFeedIdAndChallengeMember(feedId: Long, challengeMember: ChallengeMember): FeedLike?

    fun findAllByFeedId(feedId: Long): List<FeedLike>
}
