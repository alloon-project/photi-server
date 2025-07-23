package com.photi.server.domain.feed

import com.photi.server.domain.feed.custom.FeedCommentCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface FeedCommentRepository : JpaRepository<FeedComment, Long>, FeedCommentCustomRepository {

    fun findAllByFeedId(feedId: Long): List<FeedComment>
}