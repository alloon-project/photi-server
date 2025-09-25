package com.photi.core.domain.user.model.repository

import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.model.User
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface UserCustomRepository {

    fun findFetchContact(email: String?, username: String?, userId: Long?): User?

    fun find(userId: Long): User?

    fun findInfoById(userId: Long): UserInfoDto?

    fun findChallengeHistoryById(userId: Long): UserChallengeHistoryDto?

    fun findFeedsById(userId: Long): List<String>?

    fun findChallengeCntById(userId: Long): FindUserChallengeCntDto?

    fun findFeedsByDate(userId: Long, date: LocalDate): List<FindUserFeedsByDateDto>

    fun findFeedHistoryById(userId: Long, pageable: Pageable): SliceDto<FindUserFeedHistoryDto>

    fun findEndedChallengesById(
        userId: Long,
        pageable: Pageable,
    ): SliceDto<FindUserEndedChallengesDto>

    fun findUserChallengesById(userId: Long, pageable: Pageable): SliceDto<FindUserChallengesDto>

    fun findIsProveByChallengeId(userId: Long, challengeId: Long): Boolean
}
