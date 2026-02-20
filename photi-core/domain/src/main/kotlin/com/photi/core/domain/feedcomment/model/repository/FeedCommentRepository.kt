package com.photi.core.domain.feedcomment.model.repository

import com.photi.core.domain.feedcomment.model.FeedComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FeedCommentRepository : JpaRepository<FeedComment, Long>, FeedCommentCustomRepository {

    fun findByIdAndUserIdAndChallengeMemberId(
        commentId: Long,
        userId: Long,
        challengeMemberId: Long,
    ): FeedComment?

    @Modifying
    @Query("DELETE FROM FeedComment c WHERE c.feedId = :feedId")
    fun deleteByFeedId(feedId: Long)
}
