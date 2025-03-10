package com.photi.server.domain.feed.custom

import com.photi.server.common.constant.SortTypeConstants
import com.photi.server.domain.feed.Feed
import com.photi.server.service.challenge.dto.FindChallengeFeedDto
import com.photi.server.service.challenge.dto.FindChallengeFeedsDto
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.LocalDate

interface FeedCustomRepository {

    fun find(id: Long): Feed?

    fun findByFeedId(id: Long): Feed?

    fun findContentById(challengeId: Long, feedId: Long, userId: Long): FindChallengeFeedDto?

    fun findAllByChallengeId(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants
    ): Slice<Triple<LocalDate, List<FindChallengeFeedsDto>, Int>>

    fun findAllByChallengeIdV2(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants
    ): Slice<FindChallengeFeedsDto>
}