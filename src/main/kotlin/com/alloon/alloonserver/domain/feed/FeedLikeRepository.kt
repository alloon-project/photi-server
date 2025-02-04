package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.challenge.ChallengeMember
import org.springframework.data.jpa.repository.JpaRepository

interface FeedLikeRepository : JpaRepository<FeedLike, Long> {

    fun findByFeedIdAndChallengeMember(feedId: Long, challengeMember: ChallengeMember): FeedLike?
}
