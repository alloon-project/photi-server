package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.dto.FindChallengeFeedDto
import com.photi.core.domain.challenge.dto.FindChallengeFeedsDto
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.consts.SortTypeConstants
import com.photi.core.domain.feed.model.Feed
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface FeedCustomRepository {

    fun find(id: Long): Feed?

    fun findByFeedId(id: Long): Feed?

    fun findContentById(challengeId: Long, feedId: Long, userId: Long): FindChallengeFeedDto?

    fun findAllByChallengeId(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants,
    ): SliceDto<Triple<LocalDate, List<FindChallengeFeedsDto>, Int>>

    fun findAllByChallengeIdV2(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants
    ): SliceDto<FindChallengeFeedsDto>

    fun findFeedMemberCntByChallengeId(challengeId: Long): Long?
}
