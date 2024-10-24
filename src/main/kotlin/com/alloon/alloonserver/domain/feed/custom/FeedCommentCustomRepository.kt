package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.domain.feed.FeedComment
import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedCommentsDto
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedCommentCustomRepository {

    fun findByCommentId(commentId: Long): FeedComment?

    fun findAllByFeedId(feedId: Long, pageable: Pageable): Slice<FindChallengeFeedCommentsDto>
}