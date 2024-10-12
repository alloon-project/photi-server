package com.alloon.alloonserver.domain.feed

import org.springframework.data.jpa.repository.JpaRepository

interface FeedCommentRepository : JpaRepository<FeedComment, Long> {

    fun findByChallengeMemberIdAndFeedId(challengeMemberId: Long?, feedId: Long): FeedComment?
}