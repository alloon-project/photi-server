package com.alloon.alloonserver.domain.user.custom

import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.user.dto.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.LocalDate

interface UserCustomRepository {

    fun findFetchContact(email: String?, username: String?, userId: Long?): User?

    fun find(userId: Long): User?

    fun findInfoById(userId: Long): UserInfoDto?

    fun findChallengeHistoryById(userId: Long): UserChallengeHistoryDto?

    fun findFeedsById(userId: Long): List<String>?

    fun findChallengeCntById(userId: Long): FindUserChallengeCntDto?

    fun findFeedsByDate(userId: Long, date: LocalDate): List<FindUserFeedsByDateDto>

    fun findFeedHistoryById(userId: Long, pageable: Pageable): Slice<FindUserFeedHistoryDto>

    fun findEndedChallengesById(userId: Long, pageable: Pageable): Slice<FindUserEndedChallengesDto>

    fun findUserChallengesById(userId: Long, pageable: Pageable): Slice<FindUserChallengesDto>

    fun findIsProveByChallengeId(userId: Long, challengeId: Long): Boolean
}