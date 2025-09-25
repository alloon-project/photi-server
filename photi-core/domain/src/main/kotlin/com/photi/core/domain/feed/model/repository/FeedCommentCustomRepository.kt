package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeFeedCommentsDto
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.feed.model.FeedComment
import org.springframework.data.domain.Pageable

interface FeedCommentCustomRepository {

    fun findByCommentId(commentId: Long): FeedComment?

    fun findAllByFeedId(feedId: Long, pageable: Pageable): SliceDto<FindChallengeFeedCommentsDto>
}
