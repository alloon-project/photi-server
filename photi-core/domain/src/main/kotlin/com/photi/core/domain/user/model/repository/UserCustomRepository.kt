package com.photi.core.domain.user.model.repository

import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.user.dto.*
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface UserCustomRepository {

    fun findChallengeHistoryById(userId: Long): FindChallengeHistoryDto?

    fun findFeedDatesById(userId: Long): List<String>

    fun findChallengeCountById(userId: Long): FindChallengeCountDto?

    fun findFeedsByDate(userId: Long, date: LocalDate): List<FindFeedsByDateDto>

    fun findFeedHistoryById(userId: Long, pageable: Pageable): SliceDto<FindFeedHistoryDto>

    fun findEndedChallengesById(userId: Long, pageable: Pageable): SliceDto<FindEndedChallengesDto>

    fun findChallengesById(userId: Long, pageable: Pageable): SliceDto<FindChallengesDto>

    fun findChallengeIsProveById(userId: Long, challengeId: Long): Boolean
}
