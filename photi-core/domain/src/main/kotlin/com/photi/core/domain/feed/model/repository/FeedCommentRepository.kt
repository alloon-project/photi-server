package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.feed.model.FeedComment
import org.springframework.data.jpa.repository.JpaRepository

interface FeedCommentRepository : JpaRepository<FeedComment, Long>, FeedCommentCustomRepository {

    fun findAllByFeedId(feedId: Long): List<FeedComment>
}
