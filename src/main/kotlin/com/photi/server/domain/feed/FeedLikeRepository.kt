package com.photi.server.domain.feed

import com.photi.server.domain.challenge.ChallengeMember
import org.springframework.data.jpa.repository.JpaRepository

interface FeedLikeRepository : JpaRepository<FeedLike, Long> {

    fun findByFeedIdAndChallengeMember(feedId: Long, challengeMember: ChallengeMember): FeedLike?
}
