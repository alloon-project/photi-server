package com.alloon.alloonserver.domain.challenge.custom

import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.service.challenge.dto.FindChallengesDto
import com.alloon.alloonserver.service.challenge.dto.FindPopularChallengesDto
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ChallengeCustomRepository {

    fun find(id: Long): Challenge?

    fun findPopular(): List<FindPopularChallengesDto>

    fun findInfoById(id: Long): Challenge?

    fun findAllOrderByStartDate(pageable: Pageable): Slice<FindChallengesDto>
}