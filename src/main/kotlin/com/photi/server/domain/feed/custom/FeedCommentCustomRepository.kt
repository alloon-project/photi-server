package com.photi.server.domain.feed.custom

import com.photi.server.domain.feed.FeedComment
import com.photi.server.service.challenge.dto.FindChallengeFeedCommentsDto
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedCommentCustomRepository {

    fun findByCommentId(commentId: Long): FeedComment?

    fun findAllByFeedId(feedId: Long, pageable: Pageable): Slice<FindChallengeFeedCommentsDto>
}