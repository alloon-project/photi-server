package com.photi.core.domain.challenge.model.repository

import com.photi.core.domain.challenge.dto.*
import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.common.SliceDto
import org.springframework.data.domain.Pageable

interface ChallengeCustomRepository {

    fun find(id: Long): Challenge?

    fun findPopular(): List<FindPopularChallengesDto>

    fun findInfoById(id: Long): Challenge?

    fun findAllOrderByEndDate(pageable: Pageable): SliceDto<FindChallengesDto>

    fun findInvitationCodeById(id: Long): FindChallengeInvitationCodeDto?

    fun findAllByHashtag(
        hashtag: String? = null,
        popularHashtags: List<String>? = null,
        pageable: Pageable,
    ): SliceDto<FindChallengesDto>

    fun searchByName(name: String, pageable: Pageable): SliceDto<SearchChallengeByNameDto>

    fun searchByHashtag(hashtag: String, pageable: Pageable): SliceDto<SearchChallengeByHashtagDto>
}
