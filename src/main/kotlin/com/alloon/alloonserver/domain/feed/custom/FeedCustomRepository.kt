package com.alloon.alloonserver.domain.feed.custom

import com.alloon.alloonserver.common.constant.SortTypeConstants
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedDto
import com.alloon.alloonserver.service.challenge.dto.FindChallengeFeedsDto
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.LocalDate

interface FeedCustomRepository {

    fun find(id: Long): Feed?

    fun findByFeedId(id: Long): Feed?

    fun findContentById(challengeId: Long, id: Long): FindChallengeFeedDto?

    fun findAllByChallengeId(
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants
    ): Slice<Pair<LocalDate, List<FindChallengeFeedsDto>>>
}