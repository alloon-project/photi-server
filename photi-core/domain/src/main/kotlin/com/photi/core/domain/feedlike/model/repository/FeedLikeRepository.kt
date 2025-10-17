package com.photi.core.domain.feedlike.model.repository

import com.photi.core.domain.feedlike.model.FeedLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FeedLikeRepository : JpaRepository<FeedLike, Long> {

    fun findByFeedIdAndChallengeMemberId(feedId: Long, challengeMemberId: Long): FeedLike?

    @Modifying
    @Query("DELETE FROM FeedLike l WHERE l.feedId = :feedId")
    fun deleteByFeedId(feedId: Long)
}
