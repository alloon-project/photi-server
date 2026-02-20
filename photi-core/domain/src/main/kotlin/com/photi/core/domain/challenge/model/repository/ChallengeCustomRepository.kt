package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.dto.*
import com.photi.core.domain.common.SliceDto
import org.springframework.data.domain.Pageable

interface ChallengeCustomRepository {

    fun findPopularChallenges(): List<FindPopularChallengesDto>

    fun findChallengeIntroById(id: Long): FindChallengeIntroDto?

    fun findChallenges(pageable: Pageable): SliceDto<FindChallengesDto>

    fun findChallengeById(id: Long): FindChallengeDto?

    fun findChallengesByHashtags(
        popularHashtags: Set<String>,
        pageable: Pageable,
    ): SliceDto<FindChallengesDto>

    fun findChallengesBySpecificHashtag(
        hashtag: String,
        pageable: Pageable,
    ): SliceDto<FindChallengesDto>

    fun findChallengesByName(name: String, pageable: Pageable): SliceDto<FindChallengesByNameDto>

    fun findChallengesByHashtag(
        hashtag: String,
        pageable: Pageable,
    ): SliceDto<FindChallengesByHashtagDto>
}
