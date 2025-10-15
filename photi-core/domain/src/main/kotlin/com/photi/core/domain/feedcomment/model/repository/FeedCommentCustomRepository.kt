package com.photi.core.domain.feedcomment.model.repository

import com.photi.core.domain.feedcomment.dto.FindFeedCommentsDto
import com.photi.core.domain.common.SliceDto
import org.springframework.data.domain.Pageable

interface FeedCommentCustomRepository {

    fun findFeedCommentsById(feedId: Long, pageable: Pageable): SliceDto<FindFeedCommentsDto>
}
