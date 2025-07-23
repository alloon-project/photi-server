package com.photi.server.domain.challenge.custom

import com.photi.server.domain.challenge.Challenge
import com.photi.server.service.challenge.dto.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ChallengeCustomRepository {

    fun find(id: Long): Challenge?

    fun findPopular(): List<FindPopularChallengesDto>

    fun findInfoById(id: Long): Challenge?

    fun findAllOrderByEndDate(pageable: Pageable): Slice<FindChallengesDto>

    fun findInvitationCodeById(id: Long): FindChallengeInvitationCodeDto?

    fun findAllByHashtag(
        hashtag: String? = null,
        popularHashtags: List<String>? = null,
        pageable: Pageable,
    ): Slice<FindChallengesDto>

    fun searchByName(name: String, pageable: Pageable): Slice<SearchChallengeByNameDto>

    fun searchByHashtag(hashtag: String, pageable: Pageable): Slice<SearchChallengeByHashtagDto>
}