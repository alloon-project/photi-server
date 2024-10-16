package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.feed.custom.FeedCommentCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface FeedCommentRepository : JpaRepository<FeedComment, Long>, FeedCommentCustomRepository {

    fun findAllByFeedId(feedId: Long): List<FeedComment>
}