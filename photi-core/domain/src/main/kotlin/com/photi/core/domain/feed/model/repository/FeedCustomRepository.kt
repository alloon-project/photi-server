package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.feed.dto.FeedDto
import com.photi.core.domain.feed.dto.FindFeedDto
import com.photi.core.domain.feed.model.SortType
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface FeedCustomRepository {

    fun findFeedById(userId: Long, challengeId: Long, feedId: Long): FindFeedDto?

    fun findFeedsById(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortType,
    ): SliceDto<Triple<LocalDate, List<FeedDto>, Int>>

    fun findFeedsByIdV2(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortType,
    ): SliceDto<FeedDto>
}
